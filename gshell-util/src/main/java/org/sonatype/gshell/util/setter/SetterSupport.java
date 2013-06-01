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
package org.sonatype.gshell.util.setter;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

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
    protected final Logger log = Log.getLogger(getClass());

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
        log.trace("Setting '{}' on: {}, using: {}", new Object[] { value, bean, accessible });
        
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

    protected static enum Messages
    {
        ///CLOVER:OFF

        ILLEGAL_METHOD_SIGNATURE,
        ILLEGAL_FIELD_SIGNATURE,;

        private final MessageSource messages = new ResourceBundleMessageSource(SetterSupport.class);

        String format(final Object... args) {
            return messages.format(name(), args);
        }
    }
}