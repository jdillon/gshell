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

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provides a nested-namespace for command variables.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface Variables
{
  void set(String name, @Nullable Object value);

  void set(String name, @Nullable Object value, boolean mutable);

  void set(Class<?> type, @Nullable Object value);

  @Nullable
  Object get(String name);

  @Nullable
  Object get(String name, Object defaultValue);

  @Nullable
  <T> T get(String name, Class<T> type, @Nullable T defaultValue);

  @Nullable
  <T> T get(String name, Class<T> type);

  @Nullable
  <T> T get(Class<T> type, @Nullable T defaultValue);

  @Nullable
  <T> T get(Class<T> type);

  /**
   * @since 3.0
   */
  Object require(String name);

  /**
   * @since 3.0
   */
  Object require(String name, Object defaultValue);

  /**
   * @since 3.0
   */
  <T> T require(String name, Class<T> type, T defaultValue);

  /**
   * @since 3.0
   */
  <T> T require(String name, Class<T> type);

  /**
   * @since 3.0
   */
  <T> T require(Class<T> type, T defaultValue);

  /**
   * @since 3.0
   */
  <T> T require(Class<T> type);

  void unset(String name);

  void unset(Class<?> type);

  boolean contains(String name);

  boolean contains(Class<?> type);

  Iterable<String> names();

  /**
   * @since 3.0
   */
  Map<String,Object> asMap();
}
