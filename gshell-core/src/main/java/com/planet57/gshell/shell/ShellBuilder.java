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
package com.planet57.gshell.shell;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.variables.Variables;
import org.sonatype.goodies.common.ComponentSupport;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link Shell} builder.
 *
 * @since 3.0
 */
@Named
public class ShellBuilder
  extends ComponentSupport
{
  // HACK: using private injection for now; pending refactor

  @Inject
  private ShellFactory shellFactory;

  private Branding branding;

  private IO io;

  private Variables variables;

  public ShellBuilder branding(final Branding branding) {
    this.branding = branding;
    return this;
  }

  public ShellBuilder io(final IO io) {
    this.io = io;
    return this;
  }

  public ShellBuilder variables(final Variables variables) {
    this.variables = variables;
    return this;
  }

  // FIXME: need to cope with injected Provider<Variables>; CommandResolverImpl maybe others?

  @Named
  @Singleton
  public static class VariablesProvider
    implements Provider<Variables>
  {
    private Variables variables;

    public void set(final Variables variables) {
      this.variables = checkNotNull(variables);
    }

    @Override
    public Variables get() {
      checkState(variables != null);
      return variables;
    }
  }

  @Inject
  private VariablesProvider variablesProvider;

  public Shell build() {
    checkState(branding != null);
    checkState(io != null);
    checkState(variables != null);

    // HACK: expose for provider access
    variablesProvider.set(variables);

    return shellFactory.create(io, variables, branding);
  }
}
