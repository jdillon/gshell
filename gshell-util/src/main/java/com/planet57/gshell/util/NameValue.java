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
 * Container and parser for <tt>name=value</tt> bits.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class NameValue
{
  public static final String SEPARATOR = "=";

  public final String name;

  public final String value;

  private NameValue(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public static NameValue parse(final String input) {
    assert input != null;

    String name, value;

    int i = input.indexOf(SEPARATOR);
    if (i == -1) {
      name = input;
      value = Boolean.TRUE.toString();
    }
    else {
      name = input.substring(0, i);
      value = input.substring(i + 1, input.length());
    }

    return new NameValue(name.trim(), value);
  }

  public String toString() {
    return String.format("%s%s'%s'", name, SEPARATOR, value);
  }
}