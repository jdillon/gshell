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

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * A set of input, output and error streams.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class StreamSet
{
  /**
   * Output stream type.
   */
  public static enum OutputType
  {
    OUT, ERR;
  }

  public final InputStream in;

  public final PrintStream out;

  public final PrintStream err;

  public StreamSet(final InputStream in, final PrintStream out, final PrintStream err) {
    assert in != null;
    assert out != null;
    assert err != null;

    this.in = in;
    this.out = out;
    this.err = err;
  }

  public StreamSet(final InputStream in, final PrintStream out) {
    this(in, out, out);
  }

  public boolean isOutputCombined() {
    return out == err;
  }

  public InputStream getInput() {
    return in;
  }

  public PrintStream getOutput(final OutputType type) {
    assert type != null;

    switch (type) {
      case OUT:
        return out;

      case ERR:
        return err;
    }

    // Should never happen
    throw new InternalError();
  }

  public void flush() {
    Flusher.flush(out);

    if (!isOutputCombined()) {
      Flusher.flush(err);
    }
  }

  public void close() {
    Closer.close(in, out);

    if (!isOutputCombined()) {
      Closer.close(err);
    }
  }

  private String objectId(final Object obj) {
    assert obj != null;
    return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
  }

  public String toString() {
    return getClass().getSimpleName() + "{in=" + objectId(in) + ", out=" + objectId(out) + ", err=" + objectId(err) +
        "}";
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
      new PrintStream(new FileOutputStream(FileDescriptor.err)));
}