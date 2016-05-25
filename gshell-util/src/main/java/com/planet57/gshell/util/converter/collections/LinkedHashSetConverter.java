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
package com.planet57.gshell.util.converter.collections;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Converter for {@link LinkedHashSet} types.
 *
 * @since 2.0
 */
public class LinkedHashSetConverter
    extends CollectionConverterSupport
{
  public LinkedHashSetConverter() {
    super(LinkedHashSet.class);
  }

  @SuppressWarnings({"unchecked"})
  protected Object createCollection(final List list) throws Exception {
    return new LinkedHashSet(list);
  }
}
