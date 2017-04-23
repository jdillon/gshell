/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell.util.cli2.handler;

import java.lang.reflect.Constructor;

import com.planet57.gshell.util.IllegalAnnotationError;
import com.planet57.gshell.util.cli2.CliDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

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
      throw new IllegalArgumentException(
        String.format("Handler is missing required constructor: public %s(%s)", type.getName(), CliDescriptor.class.getName()));
    }
  }

  @SuppressWarnings({"unchecked"})
  public static Handler create(final CliDescriptor desc) {
    checkNotNull(desc);
    Class<? extends Handler> type = desc.getHandlerType();

    if (type == DefaultHandler.class) {
      Class valueType = desc.getSetter().getType();

      // Enum and Boolean required some special handling
      if (Enum.class.isAssignableFrom(valueType)) {
        return new EnumHandler(desc);
      }
      else if (valueType == boolean.class || valueType == Boolean.class) {
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
