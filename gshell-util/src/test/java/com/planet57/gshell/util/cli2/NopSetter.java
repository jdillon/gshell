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
package com.planet57.gshell.util.cli2;

import java.lang.reflect.AccessibleObject;

import javax.annotation.Nullable;

import com.planet57.gshell.util.setter.Setter;

/**
 * Nop {@link Setter}.
 */
public class NopSetter
    implements Setter
{
  @Override
  public void set(final Object value) throws Exception {
    // empty
  }

  @Override
  public Object getBean() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Class<?> getType() {
    return null;
  }

  @Nullable
  @Override
  public AccessibleObject getAccessible() {
    return null;
  }

  @Override
  public boolean isMultiValued() {
    return false;
  }
}
