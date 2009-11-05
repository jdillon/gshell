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

package org.sonatype.gshell.cli.handler;

import org.sonatype.gshell.cli.Descriptor;
import org.sonatype.gshell.cli.IllegalAnnotationError;
import org.sonatype.gshell.util.setter.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides access to handlers.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class Handlers
{
    private static Constructor<? extends Handler> createHandlerFactory(final Class<? extends Handler> handlerType) {
        assert handlerType != null;

        try {
            return handlerType.getConstructor(Descriptor.class, Setter.class);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Handler is missing required constructor: " + handlerType);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Handler create(final Descriptor desc, final Setter setter) {
        assert desc != null;
        assert setter != null;

        Constructor<? extends Handler> factory;
        Class<? extends Handler> handlerType = desc.getHandlerType();

        if (handlerType == Handler.class) {
            if (Enum.class.isAssignableFrom(setter.getType())) {
                // Enum requires some special handling
                return new EnumHandler(desc, setter);
            }
            else if (Boolean.class.isAssignableFrom(setter.getType()) || boolean.class.isAssignableFrom(setter.getType())) {
                // Boolean requires some special handling
                return new BooleanHandler(desc, setter);
            }
            else {
                return new ConvertingHandler(desc, setter);
            }
        }
        else {
            factory = Handlers.createHandlerFactory(handlerType);
        }

        try {
            return factory.newInstance(desc, setter);
        }
        catch (InstantiationException e) {
            throw new IllegalAnnotationError(e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalAnnotationError(e);
        }
        catch (InvocationTargetException e) {
            throw new IllegalAnnotationError(e);
        }
    }
}
