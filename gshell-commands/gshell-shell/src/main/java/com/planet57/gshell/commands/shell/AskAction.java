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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

/**
 * Ask for some input.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "ask")
public class AskAction
    extends CommandActionSupport
{
  @Option(name = "m", longName = "mask")
  private Character mask;

  @Option(name = "v", longName = "variable")
  private String variable;

  @Argument(required = true)
  private String prompt;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    // TODO: look into if there is a better way to do this to create a light-weight LineReader
    LineReader lineReader = LineReaderBuilder.builder()
      .terminal(context.getIo().getTerminal())
      .build();

    String input;
    if (mask != null) {
      input = lineReader.readLine(prompt, mask);
    }
    else {
      input = lineReader.readLine(prompt);
    }
    log.debug("Read input: {}", input);

    // set variable if configured
    if (variable != null) {
      context.getVariables().set(variable, input);
    }

    return input;
  }
}
