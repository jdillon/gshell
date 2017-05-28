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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides {@code @{style value}} expression evaluation.
 *
 * @since 3.0
 */
public class StyleExpression
{
  /**
   * Regular-expression to match {@code @{style value}}.
   */
  private static final Pattern PATTERN = Pattern.compile("@\\{([^\\s]+)\\s+([^}]+)\\}");

  private final StyleResolver resolver;

  public StyleExpression(final StyleResolver resolver) {
    this.resolver = checkNotNull(resolver);
  }

  /**
   * Evaluate expression and append to buffer.
   */
  public void evaluate(final AttributedStringBuilder buff, final String expression) {
    checkNotNull(buff);
    checkNotNull(expression);

    String input = expression;
    Matcher matcher = PATTERN.matcher(input);

    while (matcher.find()) {
      String spec = matcher.group(1);
      String value = matcher.group(2);

      // pull off the unmatched prefix of input
      int start = matcher.start(0);
      String prefix = input.substring(0, start);

      // pull off remainder from match
      int end = matcher.end(0);
      String suffix = input.substring(end, input.length());

      // resolve style
      AttributedStyle style = resolver.resolve(spec);

      // apply to buffer
      buff.append(prefix)
          .append(value, style);

      // reset matcher to the suffix of this match
      input = suffix;
      matcher.reset(input);
    }

    // append anything left over
    buff.append(input);
  }

  /**
   * Evaluate expression.
   */
  public AttributedString evaluate(final String expression) {
    AttributedStringBuilder buff = new AttributedStringBuilder();
    evaluate(buff, expression);
    return buff.toAttributedString();
  }
}
