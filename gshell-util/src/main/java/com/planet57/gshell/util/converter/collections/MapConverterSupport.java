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

import java.beans.PropertyEditor;
import java.util.Map;

import com.planet57.gshell.util.converter.ConversionException;
import com.planet57.gshell.util.converter.ConverterSupport;
import com.planet57.gshell.util.converter.basic.StringConverter;

/**
 * Support for {@link Map} converters.
 *
 * @since 2.0
 */
public abstract class MapConverterSupport
    extends ConverterSupport
{
    private final PropertyEditor keyEditor;

    private final PropertyEditor valueEditor;

    public MapConverterSupport(final Class type) {
        this(type, new StringConverter(), new StringConverter());
    }

    protected MapConverterSupport(final Class type, final PropertyEditor keyEditor, final PropertyEditor valueEditor) {
        super(type);
        this.keyEditor = keyEditor;
        this.valueEditor = valueEditor;
    }

    /**
     * Treats the text value of this property as an input stream that
     * is converted into a Property bundle.
     *
     * @return a Properties object
     * @throws ConversionException
     *          An error occurred creating the Properties object.
     */
    protected final Object convertToObject(final String text) throws Exception {
        Map map = CollectionUtil.toMap(text, keyEditor, valueEditor);
        if (map == null) {
            return null;
        }
        return createMap(map);
    }

    protected abstract Map createMap(Map map) throws Exception;

    protected final String convertToString(final Object value) {
        Map map = (Map) value;
        return CollectionUtil.toString(map, keyEditor, valueEditor);
    }
}
