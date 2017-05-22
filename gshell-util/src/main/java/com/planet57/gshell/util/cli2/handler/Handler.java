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
package com.planet57.gshell.util.cli2.handler;

import com.planet57.gshell.util.cli2.CliDescriptor;
import com.planet57.gshell.util.converter.Converters;
import com.planet57.gshell.util.setter.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides the basic mechanism to handle custom option and argument processing.
 *
 * @since 2.3
 */
public abstract class Handler
{
  private final CliDescriptor descriptor;

  protected Handler(final CliDescriptor descriptor) {
    this.descriptor = checkNotNull(descriptor);
  }

  public CliDescriptor getDescriptor() {
    return descriptor;
  }

  public Setter getSetter() {
    return getDescriptor().getSetter();
  }

  protected void set(final Object value) throws Exception {
    getSetter().set(value);
  }

  protected void set(final String value) throws Exception {
    getSetter().set(Converters.getValue(getSetter().getType(), value));
  }

  public abstract static class Input
  {
    public abstract String[] getAll();

    public String get() {
      return getAll() == null ? null : getAll()[0];
    }
  }

  public abstract void handle(String arg) throws Exception;

  public abstract String getDefaultToken();
}
