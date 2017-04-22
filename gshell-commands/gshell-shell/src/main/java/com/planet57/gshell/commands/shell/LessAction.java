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

import java.nio.file.Paths;

import javax.annotation.Nonnull;

import com.planet57.gshell.util.cli2.Option;
import org.jline.builtins.Less;
import org.jline.builtins.Source;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;

/**
 * Less action.
 *
 * @since 3.0
 */
@Command(name = "less")
public class LessAction
    extends CommandActionSupport
{
  // TODO: expose more options; see Commands.less() in jline-builtins

  @Option(name = "n", longName = "line-numbers")
  private Boolean lineNumbers;

  @Argument(required = true)
  private String source;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Less less = new Less(context.getIo().getTerminal());

    if (lineNumbers != null) {
      less.printLineNumbers = lineNumbers;
    }

    Source input = new Source.PathSource(Paths.get(source), source);
    less.run(input);

    return Result.SUCCESS;
  }
}
