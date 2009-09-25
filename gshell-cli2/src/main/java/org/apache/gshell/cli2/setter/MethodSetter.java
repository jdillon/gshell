/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.cli2.setter;

import org.apache.gshell.cli2.IllegalAnnotationError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Setter for methods.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MethodSetter
    extends SetterSupport
{
    private final Method method;

    private final Object bean;
    
    public MethodSetter(final Method method, final Object bean) {
        super(method);

        assert bean != null;
        this.bean = bean;
        assert method != null;
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

    protected void doSet(final Object value) throws IllegalAccessException {
        try {
            method.invoke(bean, value);
        }
        catch (InvocationTargetException e) {
            // Decode or wrap the target exception
            Throwable t = e.getTargetException();

            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new Error(t);
        }
    }
}
