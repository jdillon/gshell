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
package com.planet57.gshell.util.i18n;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.i18n.MessageBundle.DefaultMessage;
import com.planet57.gshell.util.i18n.MessageBundle.Key;
import org.slf4j.Logger;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nullable;

// adapted from https://github.com/sonatype/goodies/blob/master/i18n/src/main/java/org/sonatype/goodies/i18n/I18N.java

/**
 * I18n strings access.
 *
 * @since 3.0
 * @see MessageBundle
 */
public class I18N
{
  private static final Logger log = Log.getLogger(I18N.class);

  @VisibleForTesting
  static final String MISSING_MESSAGE_FORMAT = "ERROR_MISSING_MESSAGE[%s]"; //NON-NLS

  private I18N() {
    super();
  }

  /**
   * Returns a {@link MessageSource} for the given types.
   */
  public static MessageSource of(final Class... types) {
    checkNotNull(types);
    checkArgument(types.length > 0);
    return new ResourceBundleMessageSource().add(false, types);
  }

  /**
   * Returns a proxy to the given {@link MessageBundle} type.
   */
  @SuppressWarnings({"unchecked"})
  public static <T extends MessageBundle> T create(final Class<T> type) {
    checkNotNull(type);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new Handler(type));
  }

  /**
   * Proxy invocation handler to convert method calls into message lookup/format.
   */
  private static class Handler
      implements InvocationHandler
  {
    private final Class<? extends MessageBundle> type;

    private final MessageSource messages;

    public Handler(final Class<? extends MessageBundle> type) {
      this.type = checkNotNull(type);
      this.messages = I18N.of(type);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
        return method.invoke(this, args);
      }
      else if (method.getReturnType() != String.class) {
        throw new Error("Illegal MessageBundle method: " + method);
      }

      String key = getKey(method);
      String format = getFormat(key);

      if (format == null) {
        DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
        if (defaultMessage != null) {
          format = defaultMessage.value();
        }
      }

      if (format == null) {
        log.warn("Missing message for: {}, key: {}", type, key);
        return String.format(MISSING_MESSAGE_FORMAT, key);
      }

      if (args != null) {
        return String.format(format, args);
      }
      return format;
    }

    @Nullable
    private String getFormat(final String key) {
      try {
        return messages.getMessage(key);
      }
      catch (ResourceNotFoundException e) {
        log.trace("Missing resource for: {}, key: {}", type, key);
        return null;
      }
    }

    private String getKey(final Method method) {
      Key key = method.getAnnotation(Key.class);
      if (key != null) {
        return key.value();
      }
      return method.getName();
    }
  }
}
