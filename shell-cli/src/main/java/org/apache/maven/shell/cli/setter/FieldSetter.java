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

import java.lang.reflect.Field;

/**
 * Setter for fields.
 *
 * @version $Rev$ $Date$
 */
public class FieldSetter
    implements Setter
{
    protected final Field field;
    
    protected final Object bean;

    public FieldSetter(final Object bean, final Field field) {
        assert bean != null;
        assert field != null;
        
        this.bean = bean;
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

    public void set(final Object value) {
        try {
            doSet(value);
        }
        catch (IllegalAccessException ignore) {
            // try again
            field.setAccessible(true);

            try {
                doSet(value);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }

    protected void doSet(Object value) throws IllegalAccessException {
        field.set(bean, value);
    }
}
