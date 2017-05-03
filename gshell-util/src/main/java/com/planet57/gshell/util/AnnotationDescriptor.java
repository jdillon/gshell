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
package com.planet57.gshell.util;

/**
 * Base-class for annotation descriptors.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public abstract class AnnotationDescriptor
{
  public static final String UNINITIALIZED_STRING = "__EMPTY__";

  public static final char UNINITIALIZED_CHAR = '\u0000';

  /**
   * @since 3.0
   */
  public static final class UninitializedClass
  {
    // empty
  }
}
