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

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

import com.google.common.io.Flushables;
import com.planet57.gshell.util.io.StreamSet;
import org.fusesource.jansi.Ansi;
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
  /**
   * RAW underlying streams.
   */
  @Nonnull
  public final StreamSet streams;

  /**
   * Attached terminal.
   *
   * @since 3.0
   */
  @Nonnull
  public final Terminal terminal;

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

  public IO(final StreamSet streams, final Terminal terminal) {
    this.streams = ansiStreams(checkNotNull(streams));
    this.terminal = checkNotNull(terminal);

    // prepare stream references
    this.in = new InputStreamReader(this.streams.in);
    this.out = new AnsiRenderWriter(new PrintWriter(this.streams.out, true));

    /// Don't rewrite the error stream if we have the same stream for out and error
    if (streams.isOutputCombined()) {
      this.err = this.out;
    }
    else {
      this.err = new AnsiRenderWriter(new PrintWriter(this.streams.err, true));
    }
  }

  /**
   * Adapt {@link StreamSet} to be ANSI-aware if configured.
   */
  private static StreamSet ansiStreams(@Nonnull final StreamSet streams) {
    if (Ansi.isEnabled()) {
      return new StreamSet(
        streams.in,
        new PrintStream(AnsiConsole.wrapOutputStream(streams.out), true),
        new PrintStream(AnsiConsole.wrapOutputStream(streams.err), true)
      );
    }
    return streams;
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

    // TODO: terminal.flush()?
  }
}
