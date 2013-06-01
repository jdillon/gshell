/*
 * Copyright (c) 2009-2013 the original author or authors.
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
package org.sonatype.gshell.util.converter;

import java.beans.PropertyEditorSupport;

/**
 * Support for {@link Converter} implementations.
 *
 * @since 2.0
 */
public abstract class ConverterSupport
    extends PropertyEditorSupport
    implements Converter
{
    private final Class type;

    protected ConverterSupport(final Class type) {
        assert type != null;
        this.type = type;
    }

    public final Class getType() {
        return type;
    }

    public final String getAsText() {
        return toString(super.getValue());
    }

    public final void setAsText(final String text) {
        super.setValue(toObject(text));
    }

    public final Object getValue() {
        return super.getValue();
    }

    public final void setValue(final Object value) {
        // Don't validate the type. Type validation is not required by spec and some setters (e.g. Spring) expect this.
        super.setValue(value);
    }

    public final String toString(final Object value) {
        if (value == null) {
            return null;
        }

        // Don't validate the type. Type validation is not required by spec and some setters (e.g. Spring) expect this.
        return convertToString(value);
    }

    public final Object toObject(final String text) {
        if (text == null) {
            return null;
        }

        try {
            return convertToObject(text);
        }
        catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Converts the supplied object to text.  The supplied object will always be an instance of the editor type, and
     * specifically will never be null or a String (unless this is the String editor).
     *
     * @param value an instance of the editor type
     * @return the text equivalent of the value
     */
    protected String convertToString(final Object value) {
        assert value != null;
        return value.toString();
    }

    /**
     * Converts the supplied text in to an instance of the editor type.  The text will never be null, and trim() will
     * already have been called.
     *
     * @param text The text to convertToObject
     * @return An instance of the converted type
     * @throws Exception Conversion failed
     */
    protected abstract Object convertToObject(final String text) throws Exception;
}
