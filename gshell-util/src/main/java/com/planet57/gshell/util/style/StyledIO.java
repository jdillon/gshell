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
package com.planet57.gshell.util.style;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.util.io.StreamSet;
import org.jline.terminal.Terminal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Style-aware {@link IO}.
 *
 * @since 3.0
 */
public class StyledIO
    extends IO
{
  private StyledIO(final StreamSet streams, final Terminal terminal, final Reader in, final PrintWriter out, final PrintWriter err) {
    super(streams, terminal, in, out, err);
  }

  public static StyledIO create(final StyleResolver resolver, final StreamSet streams, final Terminal terminal) {
    checkNotNull(resolver);
    checkNotNull(streams);
    checkNotNull(terminal);

    Reader in = new InputStreamReader(streams.in);
    StyledWriter out = new StyledWriter(streams.out, terminal, resolver, true);
    StyledWriter err = streams.isOutputCombined() ? out : new StyledWriter(streams.err, terminal, resolver, true);

    return new StyledIO(streams, terminal, in, out, err);
  }

  public static StyledIO create(final String group, final StreamSet streams, final Terminal terminal) {
    return create(Styler.resolver(group), streams, terminal);
  }
}
