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
package com.planet57.gshell.logging;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link LoggingComponent} implementations.
 *
 * @since 2.5
 */
public class LoggingComponentSupport
    implements LoggingComponent
{
  private final String type;

  private final String name;

  private Object target;

  public LoggingComponentSupport(final String type, final String name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public LoggingComponentSupport(final String type) {
    this(type, DEFAULT_NAME);
  }

  public LoggingComponentSupport(final Object target) {
    this(target.getClass().getName());
    this.target = target;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getTarget() {
    return target;
  }

  @Override
  public int hashCode() {
    return (type + name).hashCode();
  }

  @Override
  public String toString() {
    return getType() + "{" + getName() + "}";
  }
}
