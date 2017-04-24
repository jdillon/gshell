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

import com.planet57.gshell.variables.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates {@link Evaluator} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class EvaluatorFactory
{
  public static final String TYPE = EvaluatorFactory.class.getName() + ".type";

  private static final Logger log = LoggerFactory.getLogger(EvaluatorFactory.class);

  // TODO: may need to refactor to prevent re-loading logic here, but allow the variables to be passed in
  // TODO: this could likely be simplified with Guice injection/binding

  /**
   * @sincd 3.0
   */
  public static Evaluator create(final Variables variables) {
    checkNotNull(variables);

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

      return new JexlEvaluator(variables);
    }
    catch (Exception e) {
      // ignore
    }

    return new DefaultEvaluator(variables);
  }
}
