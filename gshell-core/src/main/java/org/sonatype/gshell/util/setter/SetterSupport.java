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

import org.sonatype.gshell.util.Log;

import java.lang.reflect.AccessibleObject;

/**
 * Support for {@link Setter} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class SetterSupport
    implements Setter
{
    private final AccessibleObject accessible;

    private final Object bean;

    public SetterSupport(final AccessibleObject accessible, final Object bean) {
        assert accessible != null;
        this.accessible = accessible;
        assert bean != null;
        this.bean = bean;
    }

    public AccessibleObject getAccessible() {
        return accessible;
    }

    public Object getBean() {
        return bean;
    }

    public void set(final Object value) {
        Log.trace("Setting '", value, "' on: ", bean, " using: ", accessible);
        
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