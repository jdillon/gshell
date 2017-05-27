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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.planet57.gossip.Log;
import com.planet57.gshell.util.style.StyleBundle.DefaultStyle;
import com.planet57.gshell.util.style.StyleBundle.StyleGroup;
import com.planet57.gshell.util.style.StyleBundle.StyleName;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
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
   * Create a {@link StyleBundle} proxy.
   */
  @SuppressWarnings("unchecked")
  public static < T extends StyleBundle> T bundle(final Class<T> type) {
    checkNotNull(type);

    String group = getStyleGroup(type);
    checkState(group != null, "Style-bundle missing or invalid @StyleGroup: %s", type.getName());
    log.debug("Using style-group: {} for type: {}", group, type.getName());

    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new Handler(group));
  }

  /**
   * Proxy invocation-handler to convert method calls into string styling.
   */
  private static class Handler
      implements InvocationHandler
  {
    private final String group;

    private final StyleResolver resolver;

    public Handler(final String group) {
      this.group = checkNotNull(group);
      this.resolver = new StyleResolver(source, group);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      // Allow invocations to Object methods to pass-through
      if (method.getDeclaringClass() == Object.class) {
        return method.invoke(this, args);
      }

      // All StyleBundle methods must take exactly 1 parameter
      if (method.getParameterCount() != 1) {
        throw new InvalidStyleBundleMethodException(method, "Invalid parameters");
      }

      // All StyleBundle methods must return an AttributeString
      if (method.getReturnType() != AttributedString.class) {
        throw new InvalidStyleBundleMethodException(method, "Invalid return-type");
      }

      // resolve the style-name for method
      String styleName = getStyleName(method);

      // resolve the sourced-style, or use the default
      String style = source.get(group, styleName);
      log.debug("Sourced-style: {} -> {}", styleName, style);

      if (style == null) {
        style = getDefaultStyle(method);
        // if sourced-style was missing and default-style is missing complain
        checkState(style != null, "Style-bundle method missing @DefaultStyle: %s", method);
      }

      String value  = String.valueOf(args[0]);
      log.debug("Applying style: {} -> {} to: {}", styleName, style, value);

      AttributedStyle astyle = resolver.resolve(style);
      return new AttributedString(value, astyle);
    }
  }

  /**
   * Thrown when processing {@link StyleBundle} method is found to be invalid.
   */
  @VisibleForTesting
  static class InvalidStyleBundleMethodException
    extends RuntimeException
  {
    public InvalidStyleBundleMethodException(final Method method, final String message) {
      super(message + ": " + method);
    }
  }

  /**
   * Returns the style group-name for given type, or {@code null} if unable to determine.
   */
  @Nullable
  private static String getStyleGroup(final Class<?> type) {
    StyleGroup styleGroup = type.getAnnotation(StyleGroup.class);
    return styleGroup != null ? Strings.emptyToNull(styleGroup.value().trim()) : null;
  }

  /**
   * Returns the style-name for given method, or {@code null} if unable to determine.
   */
  private static String getStyleName(final Method method) {
    StyleName styleName = method.getAnnotation(StyleName.class);
    return styleName != null ? Strings.emptyToNull(styleName.value().trim()) : method.getName();
  }

  /**
   * Returns the default-style for given method, or {@code null} if unable to determine.
   */
  @Nullable
  private static String getDefaultStyle(final Method method) {
    DefaultStyle defaultStyle = method.getAnnotation(DefaultStyle.class);
    // allow whitespace in default-style.value, but disallow empty-string
    return defaultStyle != null ? Strings.emptyToNull(defaultStyle.value()) : null;
  }
}
