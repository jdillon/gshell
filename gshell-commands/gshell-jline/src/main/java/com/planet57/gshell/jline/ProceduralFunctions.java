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
package com.planet57.gshell.jline;

import com.planet57.gshell.functions.Functions;
import org.apache.felix.gogo.jline.Procedural;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Jline procedural functions.
 *
 * @since 3.0
 */
@Named
@Singleton
public class ProceduralFunctions
  implements Functions
{
  private final Procedural target = new Procedural();

  @Override
  public Object target() {
    return target;
  }

  @Override
  public String[] names() {
    return new String[] {
      "each", "if", "not", "throw", "try", "until", "while", "break", "continue"
    };
  }
}
