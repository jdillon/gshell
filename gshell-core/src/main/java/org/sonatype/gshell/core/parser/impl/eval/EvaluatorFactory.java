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

/**
 * Creates {@link Evaluator} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EvaluatorFactory
{
    public static final String TYPE = EvaluatorFactory.class.getName() + ".type";

    private static final Logger log = LoggerFactory.getLogger(EvaluatorFactory.class);

    private static Evaluator instance;

    public static Evaluator create() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        String className = System.getProperty(TYPE);
        if (className != null) {
            log.debug("Loading evaluator class: {}", className);

            try {
                Class type = cl.loadClass(className);
                return (Evaluator) type.newInstance();
            }
            catch (Exception e) {
                log.warn("Failed to construct evaluator of type: " + className, e);
            }
        }

        try {
            cl.loadClass("org.apache.commons.jexl.Expression");

            return new JexlEvaluator();
        }
        catch (Exception e) {
            // ignore
        }

        return new InterpolationEvaluator();
    }

    public static Evaluator get() {
        if (instance == null) {
            instance = create();
            log.debug("Using evaluator: {}", instance);
        }
        
        return instance;
    }
}