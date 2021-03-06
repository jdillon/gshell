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
package com.planet57.gshell.commands.standard;

import java.util.List;

import javax.annotation.Nonnull;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.jline.Complete;
import com.planet57.gshell.variables.Variables;

/**
 * Unset a variable or property.
 *
 * @since 2.5
 */
@Command(name = "unset", description = "Unset a variable or property")
public class UnsetAction
    extends CommandActionSupport
{
  @Option(name = "m", longName = "mode", description = "Unset mode", token = "MODE")
  private SetAction.Mode mode = SetAction.Mode.VARIABLE;

  @Argument(required = true, description = "Variable name", token = "NAME")
  @Complete("variable-name")
  private List<String> args;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Variables variables = context.getVariables();

    for (String name : args) {
      switch (mode) {
        case PROPERTY:
          log.debug("Un-setting system property: {}", name);
          System.getProperties().remove(name);
          break;

        case VARIABLE:
          log.debug("Un-setting variable: {}", name);
          variables.unset(name);
          break;
      }
    }

    return null;
  }
}
