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

package org.apache.maven.shell.cli.setter;

import org.apache.maven.shell.cli.IllegalAnnotationError;
import org.apache.maven.shell.cli.ProcessingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Setter for methods.
 *
 * @version $Rev$ $Date$
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

    public void set(final Object value) throws ProcessingException {
        try {
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
        catch (InvocationTargetException e) {
            // Decode or wrap the target exception
            Throwable t = e.getTargetException();

            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof ProcessingException) {
                throw (ProcessingException)t;
            }

            if (t != null) {
                throw new ProcessingException(t);
            }
            else {
                throw new ProcessingException(e);
            }
        }
    }
}
