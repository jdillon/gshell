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
  private final StyleSource source;

  private final String group;

  private final StyleResolver resolver;

  public StyleFactory(final StyleSource source, final String group) {
    this.source = checkNotNull(source);
    this.group = checkNotNull(group);
    this.resolver = new StyleResolver(source, group);
  }

  /**
   * Encode string with style expression.
   *
   * @see StyleExpression
   */
  //public AttributedString style(final String expression, final Object... params) {
  //  checkNotNull(expression);
  //  checkNotNull(params);
  //  // params could be empty
  //
  //  return new StyleExpression(source, group).evaluate(expression, params);
  //}

  /**
   * Encode string with style applying to formatted string.
   */
  public AttributedString style(final String style, final String format, final Object... params) {
    checkNotNull(style);
    checkNotNull(format);
    checkNotNull(params);
    // params could be empty

    String value = String.format(format, params);
    AttributedStyle astyle = resolver.resolve(style);
    return new AttributedString(value, astyle);
  }
}
