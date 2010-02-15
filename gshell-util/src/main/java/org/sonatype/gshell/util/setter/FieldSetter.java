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

package org.sonatype.gshell.util.setter;

import java.lang.reflect.Field;

/**
 * Setter for fields.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FieldSetter
    extends SetterSupport
{
    protected final Field field;

    public FieldSetter(final Field field, final Object bean) {
        super(field, bean);
        assert field != null;
        this.field = field;
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
}
