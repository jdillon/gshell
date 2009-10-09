/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sonatype.gshell.core.parser.impl.eval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.ShellHolder;

/**
 * Uses Plexus Interpolation to evaluate expressions.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class InterpolationEvaluator
    implements Evaluator
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private StringSearchInterpolator interp;

    public Object eval(String expression) throws Exception {
        // expression could be null
        
        // Skip interpolation if null or there is no start token
        if (expression == null || !expression.contains(StringSearchInterpolator.DEFAULT_START_EXPR)) {
            return expression;
        }

        if (interp == null) {
            interp = new StringSearchInterpolator();

            interp.addValueSource(new AbstractValueSource(false) {
                public Object getValue(final String expression) {
                    Variables vars = ShellHolder.get().getVariables();
                    return vars.get(expression);
                }
            });

            interp.addValueSource(new AbstractValueSource(false) {
                public Object getValue(final String expression) {
                    return System.getProperty(expression);
                }
            });
        }

        return interp.interpolate(expression);
    }
}