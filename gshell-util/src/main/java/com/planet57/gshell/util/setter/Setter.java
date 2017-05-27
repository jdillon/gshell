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
package com.planet57.gshell.util.setter;

import javax.annotation.Nullable;
import java.lang.reflect.AccessibleObject;

/**
 * Provides the basic mechanism to set values.
 *
 * @since 2.0
 */
public interface Setter
{
  void set(Object value) throws Exception;

  Object getBean();

  String getName();

  Class<?> getType();

  /**
   * Returns the underlying {@link AccessibleObject} if supported or {@code null}.
   */
  @Nullable
  AccessibleObject getAccessible();

  /**
   * Whether this setter is intrinsically multi-valued.
   */
  boolean isMultiValued();
}
