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

import java.lang.reflect.AccessibleObject;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

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
    this.accessible = checkNotNull(accessible);
    this.bean = checkNotNull(bean);
  }

  public AccessibleObject getAccessible() {
    return accessible;
  }

  public Object getBean() {
    return bean;
  }

  public void set(final Object value) {
    log.trace("Setting '{}' on: {}, using: {}", new Object[]{value, bean, accessible});

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

  protected enum Messages
  {
    ILLEGAL_METHOD_SIGNATURE,
    ILLEGAL_FIELD_SIGNATURE;

    private final MessageSource messages = new ResourceBundleMessageSource(SetterSupport.class);

    String format(final Object... args) {
      return messages.format(name(), args);
    }
  }
}
