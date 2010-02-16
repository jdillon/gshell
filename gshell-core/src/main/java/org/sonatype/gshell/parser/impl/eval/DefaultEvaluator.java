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

import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.ReplacementParser;
import org.sonatype.gshell.variables.Variables;

/**
 * Evaluates expressions using regular expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class DefaultEvaluator
    implements Evaluator
{
    private final ReplacementParser parser = new ReplacementParser()
    {
        @Override
        protected Object replace(final String key) {
            Variables vars = ShellHolder.get().getVariables();
            Object rep = vars.get(key);

            if (rep == null) {
                rep = System.getProperty(key);
            }
            return rep;
        }
    };

    public Object eval(final String expression) throws Exception {
        // expression could be null

        // Skip interpolation if null or there is no start token
        if (expression == null || !expression.contains("${")) {
            return expression;
        }

        return parser.parse(expression);
    }
}