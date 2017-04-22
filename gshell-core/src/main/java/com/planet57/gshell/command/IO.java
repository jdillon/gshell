/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet57.gshell.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

import com.google.common.io.Flushables;
import com.planet57.gshell.util.io.Closeables;
import com.planet57.gshell.util.io.StreamSet;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides access to input/output handles.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class IO
{
  @Nonnull
  public final StreamSet streams;

  /**
   * Input reader.
   */
  @Nonnull
  public final Reader in;

  /**
   * Output writer.
   */
  @Nonnull
  public final PrintWriter out;

  /**
   * Error output writer.
   */
  @Nonnull
  public final PrintWriter err;

  /**
   * The terminal associated with the given input/output.
   */
  @Nonnull
  private final Terminal terminal;

  /**
   * The verbosity setting, which commands (and framework) should inspect and respect when
   * spitting up output to the user.
   */
  private Verbosity verbosity = Verbosity.NORMAL;

  public IO(final StreamSet streams, final boolean autoFlush) {
    this.streams = checkNotNull(streams);
    this.terminal = createTerminal(streams);
    this.in = createReader(streams.in);
    this.out = createWriter(streams.out, autoFlush);

    /// Don't rewrite the error stream if we have the same stream for out and error
    if (streams.isOutputCombined()) {
      this.err = this.out;
    }
    else {
      this.err = createWriter(streams.err, autoFlush);
    }
  }

  public IO(final StreamSet streams,
            @Nullable final Terminal terminal,
            @Nullable final Reader in,
            @Nullable final PrintWriter out,
            @Nullable final PrintWriter err,
            final boolean autoFlush)
  {
    this.streams = checkNotNull(streams);

    if (terminal == null) {
      this.terminal = createTerminal(streams);
    }
    else {
      this.terminal = terminal;
    }

    if (in == null) {
      this.in = createReader(streams.in);
    }
    else {
      this.in = in;
    }

    if (out == null) {
      this.out = createWriter(streams.out, autoFlush);
    }
    else {
      this.out = out;
    }

    if (err == null) {
      this.err = createWriter(streams.err, autoFlush);
    }
    else {
      this.err = err;
    }
  }

  protected Terminal createTerminal(final StreamSet streams) {
    try {
      // FIXME: adjust for streams
      return TerminalBuilder.terminal();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected Reader createReader(final InputStream in) {
    checkNotNull(in);
    return new InputStreamReader(in);
  }

  protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
    checkNotNull(out);
    return new PrintWriter(out, autoFlush);
  }

  /**
   * Flush both output streams.
   */
  public void flush() {
    Flushables.flushQuietly(out);

    // Only attempt to flush the err stream if we aren't sharing it with out
    if (!streams.isOutputCombined()) {
      Flushables.flushQuietly(err);
    }
  }

  /**
   * Close all streams.
   */
  public void close() throws IOException {
    Closeables.close(in, out);

    // Only attempt to close the err stream if we aren't sharing it with out
    if (!streams.isOutputCombined()) {
      Closeables.close(err);
    }
  }

  public Terminal getTerminal() {
    return terminal;
  }

  //
  // Verbosity
  //

  /**
   * Defines the valid values of the {@link IO} containers verbosity settings.
   */
  public enum Verbosity
  {
    NORMAL,
    QUIET,
    SILENT
  }

  /**
   * Set the verbosity level.
   */
  public void setVerbosity(final Verbosity verbosity) {
    this.verbosity = checkNotNull(verbosity);
  }

  /**
   * Returns the verbosity level.
   */
  public Verbosity getVerbosity() {
    return verbosity;
  }

  /**
   * @since 2.5
   */
  public boolean isNormal() {
    return verbosity == Verbosity.NORMAL;
  }

  public boolean isQuiet() {
    return verbosity == Verbosity.QUIET || isSilent();
  }

  public boolean isSilent() {
    return verbosity == Verbosity.SILENT;
  }

  //
  // Output Helpers
  //

  /**
   * @since 2.5
   */
  public void println(final Object msg) {
    if (isNormal()) {
      out.println(msg);
    }
  }

  /**
   * @since 2.5
   */
  public void println(final String format, final Object... args) {
    if (isNormal()) {
      out.format(format, args).println();
    }
  }

  public void warn(final Object msg) {
    if (!isQuiet()) {
      err.println(msg);
    }
  }

  public void warn(final String format, final Object... args) {
    if (!isQuiet()) {
      err.format(format, args).println();
    }
  }

  public void error(final Object msg) {
    if (!isSilent()) {
      err.println(msg);
    }
  }

  public void error(final String format, final Object... args) {
    if (!isSilent()) {
      err.format(format, args).println();
    }
  }
}
