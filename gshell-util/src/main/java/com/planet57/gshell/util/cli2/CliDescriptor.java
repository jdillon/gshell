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

import com.planet57.gshell.util.AnnotationDescriptor;
import com.planet57.gshell.util.cli2.handler.Handler;
import com.planet57.gshell.util.cli2.handler.Handlers;
import com.planet57.gshell.util.setter.Setter;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base-class for CLI descriptors.
 *
 * @since 2.3
 */
public abstract class CliDescriptor
    extends AnnotationDescriptor
{
  private final Setter setter;

  private final String token;

  private final boolean required;

  private final String description;

  private final Class handlerType;

  private Handler handler;

  public CliDescriptor(final Object spec, final Setter setter) {
    checkNotNull(spec);
    this.setter = checkNotNull(setter);

    if (spec instanceof Option) {
      Option opt = (Option) spec;
      token = opt.token();
      required = opt.required();
      description = opt.description();
      handlerType = Handler.class == opt.handler() ? null : opt.handler();
    }
    else if (spec instanceof Argument) {
      Argument arg = (Argument) spec;
      token = arg.token();
      required = arg.required();
      description = arg.description();
      handlerType = Handler.class == arg.handler() ? null : arg.handler();
    }
    else {
      throw new IllegalArgumentException("Invalid spec: " + spec);
    }
  }

  public Setter getSetter() {
    return setter;
  }

  public boolean isMultiValued() {
    return setter.isMultiValued();
  }

  public Class getType() {
    return setter.getBean().getClass();
  }

  @Nullable
  public String getToken() {
    return !UNINITIALIZED_STRING.equals(token) ? token : null;
  }

  public boolean isRequired() {
    return required;
  }

  @Nullable
  public String getDescription() {
    return !UNINITIALIZED_STRING.equals(description) ? description: null;
  }

  public Class getHandlerType() {
    return handlerType;
  }

  public Handler getHandler() {
    if (handler == null) {
      handler = Handlers.create(this);
    }
    return handler;
  }

  public boolean isArgument() {
    return this instanceof ArgumentDescriptor;
  }

  public boolean isOption() {
    return this instanceof OptionDescriptor;
  }

  public abstract String renderSyntax();
}
