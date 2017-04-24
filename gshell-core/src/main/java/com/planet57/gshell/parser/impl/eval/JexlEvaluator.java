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

import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.util.ReplacementParser;
import com.planet57.gshell.variables.Variables;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Uses <a href="http://commons.apache.org/proper/commons-jexl/">Commons Jexl</a> to evaluate expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class JexlEvaluator
    extends ComponentSupport
    implements Evaluator
{
  private final JexlEngine jexl = new JexlBuilder().create();

  @Nullable
  public Object eval(final Variables variables, @Nullable final String expression) throws Exception {
    checkNotNull(variables);

    if (expression == null) {
      return null;
    }

    ReplacementParser parser = new ReplacementParser()
    {
      @Override
      protected Object replace(@Nonnull final String key) throws Exception {
        log.debug("Evaluating: {}", key);

        JexlContext ctx = new VariablesContext(variables);
        JexlExpression expr = jexl.createExpression(key);
        // FIXME: how we we get a jexl 1.x-style "flat-resolver"?

        Object result = expr.evaluate(ctx);

        log.debug("Result: {}", result);

        return result;
      }
    };

    return parser.parse(expression);
  }

  /**
   * Adapts {@link Variables} to {@link JexlContext}.
   */
  private static class VariablesContext
      implements JexlContext
  {
    private final Variables variables;

    public VariablesContext(final Variables variables) {
      this.variables = checkNotNull(variables);
    }

    @Override
    public Object get(final String name) {
      Object value = variables.get(name);
      if (value == null) {
        value = System.getProperty(name);
      }
      return value;
    }

    @Override
    public void set(final String name, final Object value) {
      variables.set(name, value);
    }

    @Override
    public boolean has(final String name) {
      return variables.contains(name);
    }
  }
}
