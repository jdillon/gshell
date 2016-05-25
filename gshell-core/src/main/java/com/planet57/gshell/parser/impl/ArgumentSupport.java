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
package com.planet57.gshell.parser.impl;

/**
 * Support for argument types.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class ArgumentSupport
    extends SimpleNode
{
  private String value;

  public ArgumentSupport(final int id) {
    super(id);
  }

  public ArgumentSupport(final Parser p, final int id) {
    super(p, id);
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", super.toString(), getValue());
  }

  /**
   * Returns an unquoted value.
   *
   * @param value String to unquote, must not be null; length must be at least 2
   * @return Unquoted value
   */
  protected String unquote(final String value) {
    assert value != null;
    assert value.length() >= 2;

    return value.substring(1, value.length() - 1);
  }
}