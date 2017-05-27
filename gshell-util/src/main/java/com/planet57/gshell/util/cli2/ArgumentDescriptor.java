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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Argument} descriptor.
 *
 * @since 2.3
 */
public class ArgumentDescriptor
    extends CliDescriptor
    implements Comparable<ArgumentDescriptor>
{
  private final Argument spec;

  public ArgumentDescriptor(final Argument spec, final Setter setter) {
    super(spec, setter);
    this.spec = checkNotNull(spec);
  }

  public Argument getSpec() {
    return spec;
  }

  public int getIndex() {
    return spec.index();
  }

  @Nullable
  @Override
  public String getToken() {
    String token = super.getToken();
    if (token == null) {
      return "ARG";
    }
    return token;
  }

  @Override
  public String renderSyntax() {
    return getToken();
  }

  @Override
  public int compareTo(@Nonnull final ArgumentDescriptor descriptor) {
    return Integer.compare(spec.index(), descriptor.spec.index());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "spec=" + spec +
        '}';
  }
}
