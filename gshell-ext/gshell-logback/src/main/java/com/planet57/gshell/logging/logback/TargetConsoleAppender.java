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
package com.planet57.gshell.logging.logback;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;

import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;

/**
 * Re-targatable console appender.
 *
 * This allows re-targeting the console appender, and avoid re-resolving the target stream on each append.
 *
 * Default {@link ConsoleAppender} will re-resolve the stream on each append, which causes problems for ThreadIO-based applications where this value changes.
 *
 * @since 3.0
 */
public class TargetConsoleAppender<E>
  extends OutputStreamAppender<E>
{
  private static volatile OutputStream target = System.out;
  
  private static class TargetOutputStream
    extends OutputStream
  {
    @Override
    public void write(final int b) throws IOException {
      target.write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
      target.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
      target.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      target.flush();
    }

    @Override
    public void close() throws IOException {
      target.close();
    }
  }
  
  @Override
  public void start() {
    setOutputStream(new TargetOutputStream());
    super.start();
  }
  
  public static void setTarget(final OutputStream outputStream) {
    target = checkNotNull(outputStream);
  }
}
