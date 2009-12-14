/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.parser.impl.eval;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.resolver.FlatResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.ShellHolder;
import org.sonatype.gshell.vars.Variables;
import org.sonatype.gshell.util.ReplacementParser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Uses Commons Jexl to evaluate expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class JexlEvaluator
    implements Evaluator
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ReplacementParser parser = new ReplacementParser()
    {
        @Override
        protected Object replace(final String key) throws Exception {
            assert key != null;

            log.debug("Evaluating: {}", key);

            JexlContext ctx = new Context();
            Expression expr = ExpressionFactory.createExpression(key);
            expr.addPostResolver(resolver);

            Object result = expr.evaluate(ctx);

            log.debug("Result: {}", result);

            return result;
        }
    };

    private final FlatResolver resolver = new FlatResolver(true);

    public Object eval(String expression) throws Exception {
        // expression could be null

        // Skip evaluation if null or there is no start token
        if (expression == null || !expression.contains("${")) {
            return expression;
        }

        return parser.parse(expression);
    }

    private static class Context
        implements JexlContext
    {
        private final ContextVariables vars = new ContextVariables();

        public void setVars(final Map map) {
            throw new UnsupportedOperationException();
        }

        public Map getVars() {
            return vars;
        }
    }

    private static class ContextVariables
        implements Map<String, Object>
    {
        private final Variables vars = ShellHolder.get().getVariables();

        public Object get(final Object key) {
            assert key != null;

            String name = String.valueOf(key);
            Object value;

            value = vars.get(name);
            if (value == null) {
                value = System.getProperty(name);
            }

            return value;
        }

        public Object put(final String key, final Object value) {
            assert key != null;

            Object prev = get(key);

            vars.set(key, value);

            return prev;
        }

        // Jexl only uses Map.put() and Map.get() stub everything else

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean containsKey(Object key) {
            return false;
        }

        public boolean containsValue(Object value) {
            return false;
        }

        public Object remove(Object key) {
            return null;
        }

        public void putAll(Map<? extends String, ? extends Object> m) {
            // empty
        }

        public void clear() {
            // empty
        }

        public Set<String> keySet() {
            return null;
        }

        public Collection<Object> values() {
            return null;
        }

        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }
}