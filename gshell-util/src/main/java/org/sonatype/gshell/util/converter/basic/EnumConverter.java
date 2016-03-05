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
package org.sonatype.gshell.util.converter.basic;

import org.sonatype.gshell.util.converter.ConverterSupport;

import java.lang.reflect.Method;

/**
 * Converter for {@link Enum} types.
 *
 * @since 2.0
 */
public class EnumConverter
    extends ConverterSupport
{
    public EnumConverter(final Class type) {
        super(type);
        assert type.isEnum();
    }

    protected Object convertToObject(final String text) throws Exception {
        for (Enum n : (Enum[]) getType().getEnumConstants()) {
            if (n.name().equalsIgnoreCase(text)) {
                return n;
            }
        }

        // Else try an index
        int index = Integer.parseInt(text);
        Method method = getType().getMethod("values");
        Object[] values = (Object[]) method.invoke(null);
        return values[index];
    }
}
