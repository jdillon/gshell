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
package com.planet57.gshell.alias;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Event fired once an alias has been removed.
 *
 * @since 2.5
 */
public class AliasRemovedEvent
{
  private final String name;

  public AliasRemovedEvent(final String name) {
    this.name = checkNotNull(name);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "AliasRemovedEvent{" +
      "name='" + name + '\'' +
      '}';
  }
}
