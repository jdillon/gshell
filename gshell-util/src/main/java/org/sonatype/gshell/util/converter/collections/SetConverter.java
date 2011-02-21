/**
 * Copyright (c) 2009-2011 the original author or authors.
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Converter for {@link Set} types.
 *
 * @since 2.0
 */
public class SetConverter
    extends CollectionConverterSupport
{
    public SetConverter() {
        super(Set.class);
    }

    @SuppressWarnings({"unchecked"})
    protected Object createCollection(final List list) throws Exception {
        return new LinkedHashSet(list);
    }
}
