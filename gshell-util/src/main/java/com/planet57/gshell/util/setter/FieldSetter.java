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

import java.lang.reflect.Field;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Setter for fields.
 *
 * @since 2.0
 */
public class FieldSetter
    extends SetterSupport
{
  protected final Field field;

  public FieldSetter(final Field field, final Object bean) {
    super(field, bean);
    this.field = checkNotNull(field);
  }

  public String getName() {
    return field.getName();
  }

  public Class getType() {
    return field.getType();
  }

  public boolean isMultiValued() {
    return false;
  }

  protected void doSet(final Object value) throws IllegalAccessException {
    field.set(getBean(), value);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "field=" + field +
        '}';
  }
}
