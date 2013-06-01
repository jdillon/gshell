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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Creates {@link Setter} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class SetterFactory
{
    public static Setter create(final AnnotatedElement element, final Object bean) {
        assert element != null;
        assert bean != null;

        if (element instanceof Field) {
            Field field = (Field) element;

            if (Collection.class.isAssignableFrom(field.getType())) {
                return new CollectionFieldSetter(bean, field);
            }
            else {
                return new FieldSetter(field, bean);
            }
        }
        else if (element instanceof Method) {
            Method method = (Method) element;

            return new MethodSetter(method, bean);
        }
        else {
            throw new Error();
        }
    }
}
