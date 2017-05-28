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

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Factory to create styled strings.
 *
 * @since 3.0
 */
public class StyleFactory
{
  private final StyleResolver resolver;

  public StyleFactory(final StyleResolver resolver) {
    this.resolver = checkNotNull(resolver);
  }

  /**
   * Encode string with style applying value.
   */
  public AttributedString style(final String style, final String value) {
    checkNotNull(value);
    AttributedStyle astyle = resolver.resolve(style);
    return new AttributedString(value, astyle);
  }

  /**
   * Encode string with style formatted value.
   *
   * @see #style(String, String)
   */
  public AttributedString style(final String style, final String format, final Object... params) {
    checkNotNull(format);
    checkNotNull(params);
    // params may be empty
    return style(style, String.format(format, params));
  }

  /**
   * Evaluate a style expression.
   */
  public AttributedString evaluate(final String expression) {
    checkNotNull(expression);
    return new StyleExpression(resolver).evaluate(expression);
  }

  /**
   * Evaluate a style expression with format.
   *
   * @see #evaluate(String)
   */
  public AttributedString evaluate(final String format, final Object... params) {
    checkNotNull(format);
    checkNotNull(params);
    // params may be empty
    return evaluate(String.format(format, params));
  }
}
