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
package com.planet57.gshell.util;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to process variable pattern-based replacement.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class ReplacementParser
{
  private static final String DEFAULT_PATTERN = "\\$\\{([^}]+)\\}";

  private final Pattern pattern;

  public ReplacementParser(final Pattern pattern) {
    this.pattern = checkNotNull(pattern);
  }

  public ReplacementParser() {
    this(Pattern.compile(DEFAULT_PATTERN));
  }

  @Nullable
  public String parse(@Nullable String input) {
    if (input != null && input.contains("${")) {
      Matcher matcher = pattern.matcher(input);

      while (matcher.find()) {
        Object replacment;
        try {
          replacment = replace(matcher.group(1));
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }

        if (replacment != null) {
          input = input.replace(matcher.group(0), replacment.toString());
          matcher.reset(input);
        }
      }
    }

    return input;
  }

  @Nullable
  protected abstract Object replace(String key) throws Exception;
}
