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

package org.apache.gshell.cli2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures a field or method for processing as a command-line option.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Option
{
    /** the name of the option */
    String opt() default "";

    /** the long representation of the option */
    String longOpt() default "";

    /** the name of the argument for this option */
    String argName() default "arg";

    /** description of the option */
    String description() default "";

    /** specifies whether this option is required to be present */
    boolean required() default false;

    /** specifies whether the argument value of this Option is optional */
    boolean optionalArg() default false;

    /** the number of argument values this option can have */
    int args() default org.apache.commons.cli.Option.UNINITIALIZED;

    /** the character that is the value separator */
    char valuesep() default '\u0000';

    /** the type of this Option */
    Class type() default Void.class;

    // Handler?
}