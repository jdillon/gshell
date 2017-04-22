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
import javax.inject.Inject;
import javax.inject.Provider;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.io.PromptReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Ask for some input.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "ask")
public class AskCommand
    extends CommandActionSupport
{
  private final Provider<PromptReader> promptProvider;

  @Option(name = "m", longName = "mask")
  private Character mask;

  @Option(name = "v", longName = "variable")
  private String variable;

  @Argument(required = true)
  private String prompt;

  @Inject
  public AskCommand(final Provider<PromptReader> promptProvider) {
    this.promptProvider = checkNotNull(promptProvider);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    PromptReader prompter = promptProvider.get();
    String input;

    if (mask != null) {
      input = prompter.readLine(prompt, mask);
    }
    else {
      input = prompter.readLine(prompt);
    }
    log.debug("Read input: {}", input);

    // set variable if configured
    if (variable != null) {
      context.getVariables().set(variable, input);
    }

    return input;
  }
}
