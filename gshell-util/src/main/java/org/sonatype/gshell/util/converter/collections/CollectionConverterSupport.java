/*
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

import org.sonatype.gshell.util.converter.ConverterSupport;
import org.sonatype.gshell.util.converter.basic.StringConverter;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Support for collection converters.
 *
 * @since 2.0
 */
public abstract class CollectionConverterSupport
    extends ConverterSupport
{
    private final PropertyEditor editor;

    public CollectionConverterSupport(final Class type) {
        this(type, new StringConverter());
    }

    public CollectionConverterSupport(final Class type, final PropertyEditor editor) {
        super(type);
        assert editor != null;
        this.editor = editor;
    }

    protected final Object convertToObject(final String text) throws Exception {
        List list = CollectionUtil.toList(text, editor);
        if (list == null) {
            return null;
        }

        return createCollection(list);
    }

    protected abstract Object createCollection(final List list) throws Exception;

    @SuppressWarnings({"unchecked"})
    protected final String convertToString(final Object value) {
        Collection values;
        if (value.getClass().isArray()) {
            values = new ArrayList(Array.getLength(value));
            for (int i = 0; i < Array.getLength(value); i++) {
                values.add(Array.get(value, i));
            }
        }
        else {
            values = (Collection) value;
        }

        return CollectionUtil.toString(values, editor);
    }
}
