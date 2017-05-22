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
package com.planet57.gshell.internal;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.resolver.NodePath;
import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.variables.VariableNames;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandAction} to change groups.
 *
 * @since 2.5
 */
public class ChangeGroupAction
  extends ComponentSupport
  implements CommandAction, OpaqueArguments
{
  private final String name;

  public ChangeGroupAction(final String name) {
    this.name = checkNotNull(name);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSimpleName() {
    return new NodePath(getName()).last();
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  // TODO: -h and --help probably should work sanely here, but presently do not

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    log.debug("Changing group to: {}", name);
    context.getVariables().set(VariableNames.SHELL_GROUP, name);
    return null;
  }

  @Override
  public String toString() {
    return "ChangeGroupAction{" +
      "name='" + name + '\'' +
      '}';
  }
}
