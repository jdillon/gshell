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
package com.planet57.gshell.functions;

// FIXME: this is not super ideal, but for now to avoid hard-coded hacks in ShellImpl

/**
 * A set of functions.
 *
 * This is based on GOGO function registration, where it can use reflection to bind a named function to a target instance.
 *
 * @since 3.0
 */
public interface FunctionSet
{
  /**
   * Target object which function is bound.
   */
  Object target();

  /**
   * Names of methods on {@link #target()} to expose as functions.
   */
  String[] names();
}
