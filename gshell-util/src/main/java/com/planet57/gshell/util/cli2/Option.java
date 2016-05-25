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
package com.planet57.gshell.util.cli2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.planet57.gshell.util.cli2.handler.DefaultHandler;
import com.planet57.gshell.util.cli2.handler.Handler;

import static com.planet57.gshell.util.AnnotationDescriptor.UNINITIALIZED_CHAR;
import static com.planet57.gshell.util.AnnotationDescriptor.UNINITIALIZED_STRING;

/**
 * Configures a field or method for processing as a command-line option.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Option
{
  String name() default UNINITIALIZED_STRING;

  String longName() default UNINITIALIZED_STRING;

  char separator() default UNINITIALIZED_CHAR;

  int args() default OptionDescriptor.UNINITIALIZED;

  boolean optionalArg() default false;

  String token() default UNINITIALIZED_STRING;

  boolean required() default false;

  String description() default UNINITIALIZED_STRING;

  //    String defaultValue() default UNINITIALIZED_STRING;

  boolean override() default false;

  Class<? extends Handler> handler() default DefaultHandler.class;

  // TODO: Add completer
}