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

package org.apache.maven.shell.cli.handler;

import org.apache.maven.shell.cli.Descriptor;
import org.apache.maven.shell.cli.IllegalAnnotationError;
import org.apache.maven.shell.cli.setter.Setter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to handlers.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Handlers
{
    private static final Map<Class,Constructor<? extends Handler>> handlerClasses = Collections.synchronizedMap(new HashMap<Class,Constructor<? extends Handler>>());

    private static Constructor<? extends Handler> createHandlerFactory(final Class<? extends Handler> handlerType) {
        assert handlerType != null;

        try {
            return handlerType.getConstructor(Descriptor.class, Setter.class);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Handler is missing required constructor: " + handlerType);
        }
    }

    private static Constructor<? extends Handler> getHandlerFactory(final Class type) {
        assert type != null;

        Constructor<? extends Handler> factory = handlerClasses.get(type);

        if (factory == null) {
            throw new IllegalAnnotationError("No handler registered for type: " + type);    
        }

        return factory;
    }

    @SuppressWarnings({"unchecked"})
    public static Handler create(final Descriptor desc, final Setter setter) {
        assert desc != null;
        assert setter != null;

        Constructor<? extends Handler> factory;
        Class<? extends Handler> handlerType = desc.getHandlerType();

        if (handlerType == Handler.class) {
            Class valueType = setter.getType();

            // Enum requires some special handling
            if (Enum.class.isAssignableFrom(valueType)) {
                return new EnumHandler(desc, setter, valueType);
            }

            factory = Handlers.getHandlerFactory(valueType);
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

    //
    // Registration
    //

    public static void register(final Class valueType, final Class<? extends Handler> handlerType) {
        assert valueType != null;
        assert handlerType != null;
        assert Handler.class.isAssignableFrom(handlerType);

        Constructor<? extends Handler> factory = createHandlerFactory(handlerType);

        handlerClasses.put(valueType, factory);
    }

    static {
        register(Boolean.class, BooleanHandler.class);
        register(boolean.class, BooleanHandler.class);
        register(Integer.class, IntegerHandler.class);
        register(int.class, IntegerHandler.class);
        register(Long.class, LongHandler.class);
        register(long.class, LongHandler.class);
        register(Double.class, DoubleHandler.class);
        register(double.class, DoubleHandler.class);
        register(String.class, StringHandler.class);
        register(Object.class, ObjectHandler.class);
        register(File.class, FileHandler.class);
        register(URI.class, UriHandler.class);
    }
}
