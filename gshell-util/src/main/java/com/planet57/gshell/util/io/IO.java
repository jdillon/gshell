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
package com.planet57.gshell.util.io;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.fusesource.jansi.AnsiRenderWriter;
import org.jline.terminal.Terminal;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides access to input/output handles.
 *
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
    this.streams = checkNotNull(streams);
    this.terminal = checkNotNull(terminal);

    // prepare stream references
    this.in = new InputStreamReader(streams.in);
    this.out = new AnsiRenderWriter(streams.out, true);

    // Don't rewrite the error stream if we have the same stream for out and error
    if (streams.isOutputCombined()) {
      this.err = this.out;
    }
    else {
      this.err = new AnsiRenderWriter(streams.err, true);
    }
  }

  /**
   * Flush output streams.
   */
  public void flush() {
    out.flush();

    // Only attempt to flush the err stream if we aren't sharing it with out
    if (!streams.isOutputCombined()) {
      err.flush();
    }

    terminal.flush();
  }

  //
  // Output helpers; by default everything should use {@link #out}.
  //

  /**
   * @since 3.0
   */
  public IO print(final String string) {
    out.print(string);
    return this;
  }

  /**
   * @since 3.0
   */
  public IO print(final Object obj) {
    out.print(obj);
    return this;
  }

  /**
   * @since 3.0
   */
  public IO append(final CharSequence string) {
    out.append(string);
    return this;
  }

  /**
   * @since 3.0
   */
  public IO println() {
    out.println();
    out.flush();
    return this;
  }

  /**
   * @since 3.0
   */
  public IO println(final String string) {
    out.println(string);
    out.flush();
    return this;
  }

  /**
   * @since 3.0
   */
  public IO println(final Object obj) {
    out.println(obj);
    out.flush();
    return this;
  }

  /**
   * @since 3.0
   */
  public IO format(final String format, final Object... args) {
    out.format(format, args);
    out.flush();
    return this;
  }
}
