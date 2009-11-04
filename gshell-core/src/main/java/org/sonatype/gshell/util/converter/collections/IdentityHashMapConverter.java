/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.util.converter.collections;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * ???
 *
 * @since 2.0
 */
public class IdentityHashMapConverter
    extends MapConverterSupport
{
    public IdentityHashMapConverter() {
        super(IdentityHashMap.class);
    }

    protected Map createMap(Map map) throws Exception {
        return new IdentityHashMap(map);
    }
}
