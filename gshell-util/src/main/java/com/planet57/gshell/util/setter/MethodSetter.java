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
package com.planet57.gshell.util.setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Setter for methods.
 *
 * @since 2.0
 */
public class MethodSetter
    extends SetterSupport
{
  private final Method method;

  public MethodSetter(final Method method, final Object bean) {
    super(method, bean);
    this.method = checkNotNull(method);

    if (method.getParameterTypes().length != 1) {
      throw new IllegalArgumentException(messages.ILLEGAL_METHOD_SIGNATURE(method));
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
      method.invoke(getBean(), value);
    }
    catch (InvocationTargetException e) {
      // Decode or wrap the target exception
      Throwable t = e.getTargetException();

      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      }
      if (t instanceof Error) {
        throw (Error) t;
      }
      throw new Error(t);
    }
  }

  @Override
  public String toString() {
    return "MethodSetter{" +
      "method=" + method +
      '}';
  }
}
