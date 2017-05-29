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

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Provides the source of style configuration.
 *
 * @since 3.0
 */
public interface StyleSource
{
  /**
   * Returns the appropriate style for the given style-group and style-name, or {@code null} if missing.
   */
  @Nullable
  String get(String group, String name);

  /**
   * Set a specific style in a style-group.
   */
  void set(String group, String name, String style);

  /**
   * Remove all styles for given style-group.
   */
  void remove(String group);

  /**
   * Remove a specific style from style-group.
   */
  void remove(String group, String name);

  /**
   * Clear all styles.
   */
  void clear();

  /**
   * Returns configured style-group names.
   *
   * @return Immutable collection.
   */
  Iterable<String> groups();

  /**
   * Returns configured styles for given style-group.
   *
   * @return Immutable map.
   */
  Map<String,String> styles(String group);
}
