/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.util.cli2.handler;

import org.sonatype.gshell.util.IllegalAnnotationError;
import org.sonatype.gshell.util.cli2.CliDescriptor;

import java.lang.reflect.Constructor;

/**
 * Provides access to handlers.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class Handlers
{
    private static Constructor<? extends Handler> createHandlerFactory(final Class<? extends Handler> type) {
        assert type != null;

        try {
            return type.getConstructor(CliDescriptor.class);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Handler is missing required constructor: " + type);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Handler create(final CliDescriptor desc) {
        assert desc != null;

        Class<? extends Handler> type = desc.getHandlerType();

        if (type == DefaultHandler.class) {
            Class valueType = desc.getSetter().getType();

            // Enum and Boolean required some special handling
            if (Enum.class.isAssignableFrom(valueType)) {
                return new EnumHandler(desc);
            }
            else if (boolean.class.isAssignableFrom(valueType) || Boolean.class.isAssignableFrom(valueType)) {
                return new BooleanHandler(desc);
            }
            else {
                return new DefaultHandler(desc);
            }
        }

        Constructor<? extends Handler> factory = Handlers.createHandlerFactory(type);

        try {
            return factory.newInstance(desc);
        }
        catch (Exception e) {
            throw new IllegalAnnotationError("Unable to construct handler: " + type, e);
        }
    }
}