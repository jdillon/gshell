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

package org.apache.maven.shell.cli2.setter;

import java.lang.reflect.AccessibleObject;

/**
 * Support for {@link Setter} implementations.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class SetterSupport
    implements Setter
{
    private final AccessibleObject accessible;

    public SetterSupport(final AccessibleObject accessible) {
        assert accessible != null;
        this.accessible = accessible;
    }

    public void set(final Object value) {
        try {
            doSet(value);
        }
        catch (IllegalAccessException ignore) {
            // try again
            accessible.setAccessible(true);

            try {
                doSet(value);
            }
            catch (IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
    }

    protected abstract void doSet(Object value) throws IllegalAccessException;
}