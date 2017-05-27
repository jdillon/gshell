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
package com.planet57.gshell.commands.shell;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.io.PromptHelper;

/**
 * Ask for some input.
 *
 * @since 2.0
 */
@Command(name = "ask", description = "Ask for some input")
public class AskAction
    extends CommandActionSupport
{
  @Nullable
  @Option(name = "m", longName = "mask", description = "Input mask character", token = "CHAR")
  private Character mask;

  @Nullable
  @Option(name = "v", longName = "variable", description = "Set result to input", token = "NAME")
  private String variable;

  @Argument(required = true, description = "Input prompt", token = "PROMPT")
  private String prompt;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    PromptHelper promptHelper = new PromptHelper(context.getIo().terminal);

    String input;
    if (mask != null) {
      input = promptHelper.readLine(prompt, mask);
    }
    else {
      input = promptHelper.readLine(prompt);
    }
    log.debug("Read input: {}", input);

    // set variable if configured
    if (variable != null) {
      context.getVariables().set(variable, input);
    }

    return input;
  }
}
