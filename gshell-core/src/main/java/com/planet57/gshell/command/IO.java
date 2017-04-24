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
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

import com.google.common.io.Flushables;
import com.planet57.gshell.util.io.Closeables;
import com.planet57.gshell.util.io.StreamSet;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderWriter;
import org.jline.terminal.Terminal;

import javax.annotation.Nonnull;

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

  @Nonnull
  private final Terminal terminal;

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
   * Error-output writer.
   */
  @Nonnull
  public final PrintWriter err;

    /**
   * The verbosity setting, which commands (and framework) should inspect and respect when
   * spitting up output to the user.
   */
  private Verbosity verbosity = Verbosity.NORMAL;

  public IO(final StreamSet streams, final Terminal terminal) {
    this.streams = ansiStreams(checkNotNull(streams));
    this.terminal = checkNotNull(terminal);
    this.in = new InputStreamReader(streams.in);
    this.out = new AnsiRenderWriter(new PrintWriter(streams.out, true));

    /// Don't rewrite the error stream if we have the same stream for out and error
    if (streams.isOutputCombined()) {
      this.err = this.out;
    }
    else {
      this.err = new AnsiRenderWriter(new PrintWriter(streams.err, true));
    }
  }

  private static StreamSet ansiStreams(@Nonnull final StreamSet streams) {
    return new StreamSet(
      streams.in,
      new PrintStream(AnsiConsole.wrapOutputStream(streams.out), true),
      new PrintStream(AnsiConsole.wrapOutputStream(streams.err), true)
    );
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

  // FIXME: refine/redo verbosity

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

//  /**
//   * Returns the verbosity level.
//   */
//  public Verbosity getVerbosity() {
//    return verbosity;
//  }

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

//  public void warn(final Object msg) {
//    if (!isQuiet()) {
//      err.println(msg);
//    }
//  }

//  public void warn(final String format, final Object... args) {
//    if (!isQuiet()) {
//      err.format(format, args).println();
//    }
//  }

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
