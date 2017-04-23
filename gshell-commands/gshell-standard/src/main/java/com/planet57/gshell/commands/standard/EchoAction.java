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

import java.util.Iterator;
import java.util.List;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;

import javax.annotation.Nonnull;

/**
 * Print all arguments to the commands standard output.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "echo")
public class EchoAction
    extends CommandActionSupport
{
  @Option(name = "n")
  private boolean noTrailingNewline;

  @Argument()
  private List<String> args;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    if (args != null && !args.isEmpty()) {
      Iterator iter = args.iterator();

      while (iter.hasNext()) {
        io.out.print(iter.next());
        if (iter.hasNext()) {
          io.out.print(" ");
        }
      }
    }

    if (!noTrailingNewline) {
      io.out.println();
    }

    return Result.SUCCESS;
  }
}
