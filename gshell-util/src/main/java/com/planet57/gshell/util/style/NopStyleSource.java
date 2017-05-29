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

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * {@link StyleSource} which always returns {@code null}.
 *
 * @since 3.0
 */
public class NopStyleSource
  implements StyleSource
{
  /**
   * Always returns {@code null}.
   */
  @Nullable
  @Override
  public String get(final String group, final String name) {
    return null;
  }

  /**
   * Always returns empty list.
   */
  @Override
  public Iterable<String> groups() {
    return Collections.emptyList();
  }

  /**
   * Always returns empty map.
   */
  @Override
  public Map<String, String> styles(final String group) {
    return Collections.emptyMap();
  }
}
