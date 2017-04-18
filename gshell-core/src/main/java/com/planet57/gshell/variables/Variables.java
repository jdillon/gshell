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

import java.util.Iterator;

/**
 * Provides a nested-namespace for command variables.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface Variables
{
  void set(String name, Object value);

  void set(String name, Object value, boolean mutable);

  void set(Class<?> type, Object value);

  Object get(String name);

  Object get(String name, Object defaultValue);

  <T> T get(String name, Class<T> type, T defaultValue);

  <T> T get(String name, Class<T> type);

  <T> T get(Class<T> type, T defaultValue);

  <T> T get(Class<T> type);

  void unset(String name);

  void unset(Class<?> type);

  boolean contains(String name);

  boolean contains(Class<?> type);

  boolean isMutable(String name);

  boolean isMutable(Class<?> type);

  boolean isCloaked(String name);

  boolean isCloaked(Class<?> type);

  Iterator<String> names();

  Variables parent();

  /**
   * Throw to indicate that a variable change was attempted but the variable was not muable.
   */
  class ImmutableVariableException
      extends RuntimeException
  {
    public ImmutableVariableException(String name) {
      super(name);
    }
  }
}
