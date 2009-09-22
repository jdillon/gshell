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

package org.apache.maven.shell.bootstrap;

import java.util.Properties;

//
// NOTE: Most of this stuff came from org.apache.log4j.helpers.OptionConverter.
//

/**
 * Simple expression evaluator for handling <tt>${variable}</tt> expansion.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ExpressionEvaluator
{
    private static final String DELIM_START = "${";

    private static final int DELIM_START_LEN = 2;

    private static final char DELIM_STOP = '}';

    private static final int DELIM_STOP_LEN = 1;

    private final Properties props;

    public ExpressionEvaluator(final Properties props) {
        // props could be null
        this.props = props;
    }

    public String evaluate(final String input) throws IllegalArgumentException {
        return evaluate(input, props);
    }
    
    public static String evaluate(final String input, final Properties props) throws IllegalArgumentException {
        StringBuilder buff = new StringBuilder();

        int i = 0;
        int j, k;

        while (true) {
            j = input.indexOf(DELIM_START, i);
            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return input;
                }
                else { // add the tail string which contains no variables and return the result.
                    buff.append(input.substring(i, input.length()));
                    return buff.toString();
                }
            }
            else {
                buff.append(input.substring(i, j));
                k = input.indexOf(DELIM_STOP, j);

                if (k == -1) {
                    throw new IllegalArgumentException('"' + input + "\" has no closing brace. Opening brace at position " + j);
                }
                else {
                    j += DELIM_START_LEN;
                    String key = input.substring(j, k);

                    // first try in System properties
                    String replacement = getSystemProperty(key);

                    // then try props parameter
                    if (replacement == null && props != null) {
                        replacement = props.getProperty(key);
                    }

                    if (replacement != null) {
                        // Do variable substitution on the replacement string
                        // such that we can solve "Hello ${x2}" as "Hello p1"
                        // the where the properties are
                        // x1=p1
                        // x2=${x1}
                        String recursiveReplacement = evaluate(replacement, props);
                        buff.append(recursiveReplacement);
                    }

                    i = k + DELIM_STOP_LEN;
                }
            }
        }
    }

    public static String getSystemProperty(final String key, final String def) {
        try {
            return System.getProperty(key, def);
        }
        catch (Throwable e) {
            Log.debug("Was not allowed to read system property: ", key);
            return def;
        }
    }

    public static String getSystemProperty(final String key) {
        return getSystemProperty(key, null);
    }
}