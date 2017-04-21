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

import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.ComponentSupport;
import com.planet57.gshell.util.ReplacementParser;
import com.planet57.gshell.variables.Variables;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Uses <a href="http://commons.apache.org/proper/commons-jexl/">Commons Jexl</a> to evaluate expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class JexlEvaluator
    extends ComponentSupport
    implements Evaluator
{
  private final ReplacementParser parser = new ReplacementParser()
  {
    private final JexlEngine jexl = new JexlBuilder().create();

    @Override
    protected Object replace(final String key) throws Exception {
      assert key != null;

      log.debug("Evaluating: {}", key);

      JexlContext ctx = new VariablesContext(ShellHolder.get().getVariables());
      JexlExpression expr = jexl.createExpression(key);
      // FIXME: how we we get a jexl 1.x-style "flat-resolver"?

      Object result = expr.evaluate(ctx);

      log.debug("Result: {}", result);

      return result;
    }
  };

  @Nullable
  public Object eval(@Nullable final String expression) throws Exception {
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
