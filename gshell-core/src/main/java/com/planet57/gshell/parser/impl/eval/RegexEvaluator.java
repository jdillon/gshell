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
package com.planet57.gshell.parser.impl.eval;

import com.planet57.gshell.util.ReplacementParser;
import com.planet57.gshell.variables.Variables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Evaluates expressions using regular expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class RegexEvaluator
    implements Evaluator
{
  @Nullable
  public Object eval(final Variables variables, @Nullable final String expression) throws Exception {
    checkNotNull(variables);

    if (expression == null) {
      return null;
    }

    ReplacementParser parser = new ReplacementParser()
    {
      @Override
      protected Object replace(@Nonnull final String key) {
        Object replacement = variables.get(key);
        if (replacement == null) {
          replacement = System.getProperty(key);
        }
        return replacement;
      }
    };

    return parser.parse(expression);
  }
}
