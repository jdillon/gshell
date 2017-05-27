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
package com.planet57.gshell.variables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Event fired once a variable has been unset.
 *
 * @since 2.0
 */
public class VariableUnsetEvent
{
  private final String name;

  public VariableUnsetEvent(final String name) {
    this.name = checkNotNull(name);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "name='" + name + '\'' +
        '}';
  }
}
