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

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Converter for {@link SortedMap} types.
 *
 * @since 2.0
 */
public class SortedMapConverter
    extends MapConverterSupport
{
  public SortedMapConverter() {
    super(SortedMap.class);
  }

  @SuppressWarnings({"unchecked"})
  protected Map createMap(final Map map) throws Exception {
    return new TreeMap(map);
  }
}
