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
package org.sonatype.gshell.util.setter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Setter for fields of collection types.  Currently supports lists and sets.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CollectionFieldSetter
    extends FieldSetter
{
    public CollectionFieldSetter(final Object bean, final Field field) {
        super(field, bean);

        if (!Collection.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException(Messages.ILLEGAL_FIELD_SIGNATURE.format(field.getType()));
        }
    }

    public boolean isMultiValued() {
        return true;
    }

    public Class getType() {
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            type = ptype.getActualTypeArguments()[0];

            if (type instanceof Class) {
                return (Class) type;
            }
        }

        // If collection types don't have a parameter type, then the ObjectHandler will be used
        // which basically is the same as the StringHandler
        return Object.class;
    }

    protected void doSet(final Object value) throws IllegalAccessException {
        Object obj = field.get(getBean());

        // If the field is not set, then create a new instance of the collection and set it
        if (obj == null) {
            Class type = field.getType();

            if (List.class.isAssignableFrom(type)) {
                obj = new ArrayList();
            }
            else if (Set.class.isAssignableFrom(type)) {
                obj = new LinkedHashSet();
            }
            else if (Collection.class.isAssignableFrom(type)) {
                obj = new ArrayList();
            }
            else {
                try {
                    obj = type.newInstance();
                }
                catch (Exception e) {
                    throw new IllegalStateException("Unsupported collection type: " + field.getType(), e);
                }
            }

            field.set(getBean(), obj);
        }

        // This should never happen
        if (!(obj instanceof Collection)) {
            throw new IllegalStateException("Field is not a collection type: " + field);
        }

        // noinspection unchecked
        ((Collection) obj).add(value);
    }
}
