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

import com.planet57.gshell.util.setter.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Option} descriptor.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class OptionDescriptor
    extends CliDescriptor
{
  public static final int UNINITIALIZED = org.apache.commons.cli.Option.UNINITIALIZED;

  public static final int UNLIMITED = org.apache.commons.cli.Option.UNLIMITED_VALUES;

  private final Option spec;

  private final String name;

  private final String longName;

  private final int args;

  private final boolean optionalArg;

  public OptionDescriptor(final Option spec, final Setter setter) {
    super(spec, setter);
    this.spec = checkNotNull(spec);
    this.name = UNINITIALIZED_STRING.equals(spec.name()) ? null : spec.name();
    this.longName = UNINITIALIZED_STRING.equals(spec.longName()) ? null : spec.longName();

    Class type = setter.getType();

    if (spec.args() != UNINITIALIZED) {
      args = spec.args();
    }
    else {
      if (type == UninitializedClass.class) {
        args = 0;
      }
      else if (type == boolean.class || type == Boolean.class) {
        if (spec.optionalArg()) {
          args = 1;
        }
        else {
          args = spec.args();
        }
      }
      else if (setter.isMultiValued()) {
        args = UNLIMITED;
      }
      else {
        args = 1;
      }
    }

    optionalArg = spec.optionalArg();
  }

  public Option getSpec() {
    return spec;
  }

  public String getName() {
    return name;
  }

  public String getLongName() {
    return longName;
  }

  public char getSeparator() {
    return spec.separator();
  }

  public int getArgs() {
    return args;
  }

  public boolean isArgumentOptional() {
    return optionalArg;
  }

  public boolean getOverride() {
    return spec.override();
  }

  @Override
  public String getSyntax() {
    if (name != null && longName != null) {
      return String.format("-%s (--%s)", name, longName);
    }
    else if (name != null) {
      return String.format("-%s", name);
    }
    else if (longName != null) {
      return String.format("--%s", longName);
    }
    throw new Error();
  }
}
