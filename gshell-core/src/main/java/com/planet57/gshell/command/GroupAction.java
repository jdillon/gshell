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
package com.planet57.gshell.command;

import com.planet57.gshell.command.resolver.NodePath;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.variables.VariableNames;
import jline.console.completer.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandAction} to switch groups.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class GroupAction
    implements CommandAction, OpaqueArguments
{
  private static final Logger log = LoggerFactory.getLogger(GroupAction.class);

  private final String name;

  public GroupAction(final String name) {
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

  @Override
  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);

    log.debug("Changing group to: {}", name);
    context.getVariables().set(VariableNames.SHELL_GROUP, name);

    return Result.SUCCESS;
  }

  @Override
  public MessageSource getMessages() {
    return null;
  }

  @Override
  public Completer[] getCompleters() {
    return new Completer[0];
  }

  @Override
  public CommandAction copy() {
    try {
      return (CommandAction) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
}
