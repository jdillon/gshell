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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides {@code @{style format}} expression evaluation.
 *
 * @since 3.0
 */
public class StyleExpression
{
  private static final Pattern PATTERN = Pattern.compile("\\@\\{([^}]+)\\}");

  private final StyleSource source;

  private final String group;

  public StyleExpression(final StyleSource source, final String group) {
    this.source = checkNotNull(source);
    this.group = checkNotNull(group);
  }

  public AttributedString evaluate(final String expression, final Object... params) {
    checkNotNull(expression);
    checkNotNull(params);
    // params could be empty

    String value = String.format(expression, params);
    Matcher matcher = PATTERN.matcher(value);
    AttributedStringBuilder buff = new AttributedStringBuilder();
    while (matcher.find()) {
      // TODO:
    }

    return buff.toAttributedString();
  }
}
