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
package com.planet57.gshell.util.jline;

import org.jline.builtins.Source;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to construct a {@link Source} around a raw {@link InputStream}.
 *
 * @since 3.0
 */
public class InputStreamSource
  implements Source
{
  private final String name;

  private final InputStream stream;

  public InputStreamSource(final String name, final InputStream stream) {
    this.name = checkNotNull(name);
    this.stream = checkNotNull(stream);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InputStream read() throws IOException {
    return stream;
  }
}
