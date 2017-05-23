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
package com.planet57.gshell.functions;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link FunctionSet} implementations.
 *
 * @since 3.0
 */
public class FunctionSetSupport
  implements FunctionSet
{
  private final Object target;

  private final String[] names;

  public FunctionSetSupport(final Object target, final String... names) {
    this.target = checkNotNull(target);
    checkNotNull(names);
    checkArgument(names.length != 0);
    this.names = names;
  }

  @Override
  public Object target() {
    return target;
  }

  @Override
  public String[] names() {
    return names;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "names=" + Arrays.toString(names) +
        '}';
  }
}
