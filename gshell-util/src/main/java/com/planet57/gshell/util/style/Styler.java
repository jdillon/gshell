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

import javax.annotation.Nullable;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.style.StyleBundle.DefaultStyle;
import com.planet57.gshell.util.style.StyleBundle.StyleGroup;
import com.planet57.gshell.util.style.StyleBundle.StyleName;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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

  private static volatile StyleSource source = new NopStyleSource();

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
   * Returns global {@link StyleSource}.
   */
  public static StyleSource getSource() {
    return source;
  }

  /**
   * Create a factory for the given style-group.
   */
  public static StyleFactory factory(final String group) {
    return new StyleFactory(source, group);
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
    private final String group;

    public Handler(final Class<? extends StyleBundle> type) {
      checkNotNull(type);

      // resolve the style-group and style-factory
      this.group = getStyleGroup(type);
      checkState(group != null, "Style-bundle missing @StyleGroup: %s", type.getName());
      log.debug("Using style-group: {} for type: {}", group, type.getName());
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      // Allow invocations to Object methods to pass-through
      if (method.getDeclaringClass() == Object.class) {
        return method.invoke(this, args);
      }

      // All StyleBundle methods must return an AttributeString
      if (method.getReturnType() != AttributedString.class) {
        throw new Error("Illegal StyleBundle method: " + method);
      }

      // resolve the style name for method
      String styleName = getStyleName(method);

      // resolve the configured style, or use the default
      String style = source.get(group, styleName);
      if (style == null) {
        style = getDefaultStyle(method);
        // if configured style was missing and default-style is missing complain
        checkState(style != null, "Style-bundle method missing @DefaultStyle: %s", method);
      }
      log.debug("Using style: {} -> {}", styleName, style);

      // TODO: need to resolve issues with expression vs factory exposed style lookup

      return null;
    }

    @Nullable
    private static String getStyleGroup(final Class<?> type) {
      StyleGroup group = type.getAnnotation(StyleGroup.class);
      return group != null ? group.value() : null;
    }

    private static String getStyleName(final Method method) {
      StyleName name = method.getAnnotation(StyleName.class);
      return name != null ? name.value() : method.getName();
    }

    @Nullable
    private static String getDefaultStyle(final Method method) {
      DefaultStyle style = method.getAnnotation(DefaultStyle.class);
      return style != null ? style.value() : null;
    }
  }
}