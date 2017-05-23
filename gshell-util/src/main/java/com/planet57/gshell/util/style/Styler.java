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
package com.planet57.gshell.util.style;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.planet57.gossip.Log;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Style facade.
 *
 * @since 3.0
 * @see StyleBundle
 * @see StyleFactory
 * @see StyleSource
 */
public class Styler
{
  private static final Logger log = Log.getLogger(Styler.class);

  private static StyleSource source = new DefaultStyleSource();

  private Styler() {
    // empty
  }

  /**
   * Install global {@link StyleSource}.
   */
  public static void setSource(final StyleSource source) {
    Styler.source = checkNotNull(source);
    log.debug("Source: {}", source);
  }

  /**
   * Returns previously configured {@link StyleSource}.
   */
  public static StyleSource getSource() {
    return source;
  }

  /**
   * Create a factory for the given style group.
   */
  public static StyleFactory factory(final String group) {
    checkNotNull(group);
    return new StyleFactory(null);
  }

  /**
   * Create a style-bundle proxy.
   */
  @SuppressWarnings("unchecked")
  public static < T extends StyleBundle> T bundle(final Class<T> type) {
    checkNotNull(type);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new Handler(type));
  }

  /**
   * Proxy invocation-handler to convert method calls into string styling.
   */
  private static class Handler
      implements InvocationHandler
  {
    private final Class<? extends StyleBundle> type;

    public Handler(final Class<? extends StyleBundle> type) {
      this.type = checkNotNull(type);

      // TODO: resolve group and style-source
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
        return method.invoke(this, args);
      }
      else if (method.getReturnType() != AttributedString.class) {
        throw new Error("Illegal StyleBundle method: " + method);
      }

      // TODO:

      return null;
    }
  }
}
