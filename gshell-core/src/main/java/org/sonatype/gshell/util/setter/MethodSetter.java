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

import org.sonatype.gshell.util.IllegalAnnotationError;

import java.lang.reflect.Method;

/**
 * Setter for methods.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class MethodSetter
    implements Setter
{
    private final Object bean;
    
    private final Method method;

    public MethodSetter(final Object bean, final Method method) {
        assert bean != null;
        assert method != null;
        
        this.bean = bean;
        this.method = method;
        
        if (method.getParameterTypes().length != 1) {
            throw new IllegalAnnotationError(Messages.ILLEGAL_METHOD_SIGNATURE.format(method));
        }
    }

    public String getName() {
        return method.getName();
    }

    public Class getType() {
        return method.getParameterTypes()[0];
    }

    public boolean isMultiValued() {
        return false;
    }

    public void set(final Object value) throws Exception {
        try {
            method.invoke(bean, value);
        }
        catch (IllegalAccessException ignore) {
            method.setAccessible(true);

            try {
                method.invoke(bean, value);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }
}
