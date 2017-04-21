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

import java.io.PrintStream;
import java.io.PrintWriter;

import com.planet57.gshell.util.io.StreamSet;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ANSI-aware {@link IO}.
 *
 * This will setup support to parse {@code @|code text|} strings with {@link AnsiRenderWriter}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class AnsiIO
    extends IO
{
  public AnsiIO(final StreamSet streams, final boolean autoFlush) {
    super(ansiStreams(streams), autoFlush);
  }

  private static StreamSet ansiStreams(final StreamSet streams) {
    checkNotNull(streams);
    return new StreamSet(streams.in, wrap(streams.out), wrap(streams.err));
  }

  private static PrintStream wrap(final PrintStream stream) {
    checkNotNull(stream);
    return new PrintStream(AnsiConsole.wrapOutputStream(stream));
  }

  @Override
  protected PrintWriter createWriter(final PrintStream out, final boolean autoFlush) {
    checkNotNull(out);
    return new AnsiRenderWriter(out, autoFlush);
  }
}
