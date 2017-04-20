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
package com.planet57.gshell.util.converter.basic;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.List;
import java.util.ListIterator;

import com.planet57.gshell.util.converter.collections.CollectionConverterSupport;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Converter for arrays.
 *
 * @since 2.0
 */
public class ArrayConverter
    extends CollectionConverterSupport
{
  public ArrayConverter(final Class type, final PropertyEditor editor) {
    super(type, editor);
    checkArgument(type.isArray());
    checkArgument(type.getComponentType().isArray());
  }

  protected Object createCollection(final List list) {
    Object array = Array.newInstance(getType().getComponentType(), list.size());
    for (ListIterator iterator = list.listIterator(); iterator.hasNext(); ) {
      Object item = iterator.next();
      int index = iterator.previousIndex();
      Array.set(array, index, item);
    }
    return array;
  }
}
