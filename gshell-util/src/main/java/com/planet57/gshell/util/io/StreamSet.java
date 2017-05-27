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

import com.google.common.io.Flushables;

import javax.annotation.Nonnull;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A set of input, output and error streams.
 *
 * @since 2.0
 */
public class StreamSet
{
  /**
   * Output stream type.
   */
  public enum OutputType
  {
    OUT, ERR
  }

  @Nonnull
  public final InputStream in;

  @Nonnull
  public final PrintStream out;

  @Nonnull
  public final PrintStream err;

  public StreamSet(final InputStream in, final PrintStream out, final PrintStream err) {
    this.in = checkNotNull(in);
    this.out = checkNotNull(out);
    this.err = checkNotNull(err);
  }

  public StreamSet(final InputStream in, final PrintStream out) {
    this(in, out, out);
  }

  public InputStream getInput() {
    return in;
  }

  public boolean isOutputCombined() {
    return out == err;
  }

  public PrintStream getOutput(final OutputType type) {
    checkNotNull(type);

    switch (type) {
      case OUT:
        return out;

      case ERR:
        return err;
    }

    // unreachable
    throw new Error();
  }

  public void flush() {
    Flushables.flushQuietly(out);

    if (!isOutputCombined()) {
      Flushables.flushQuietly(err);
    }
  }

  public void close() {
    Closeables.close(in, out);

    if (!isOutputCombined()) {
      Closeables.close(err);
    }
  }

  private String objectId(final Object obj) {
    assert obj != null;
    return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
  }

  public String toString() {
    return String.format("%s{in=%s, out=%s, err=%s}",
      getClass().getSimpleName(), objectId(in), objectId(out), objectId(err)
    );
  }

  /**
   * Create a new stream set as {@link System} is currently configured.
   */
  public static StreamSet system() {
    return new StreamSet(System.in, System.out, System.err);
  }

  /**
   * Install the given stream set as the {@link System} streams.
   */
  public static void system(final StreamSet streams) {
    assert streams != null;

    System.setIn(streams.in);
    System.setOut(streams.out);
    System.setErr(streams.err);
  }

  /**
   * The original {@link System} streams (as they were when this class loads).
   */
  public static final StreamSet SYSTEM = system();

  /**
   * The {@link System} streams as file streams, for a better chance of non-buffered I/O.
   */
  public static final StreamSet SYSTEM_FD = new StreamSet(
      new FileInputStream(FileDescriptor.in),
      new PrintStream(new FileOutputStream(FileDescriptor.out)),
      new PrintStream(new FileOutputStream(FileDescriptor.err))
  );
}
