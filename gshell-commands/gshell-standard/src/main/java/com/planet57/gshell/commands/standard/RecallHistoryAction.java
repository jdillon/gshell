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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import org.jline.reader.History;

import javax.annotation.Nonnull;

/**
 * Recall history.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "recall")
public class RecallHistoryAction
    extends CommandActionSupport
{
  @Argument(required = true)
  private int index;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    History history = context.getShell().getHistory();

    if (index < 1 || index > history.size()) {
      io.err.println(getMessages().format("error.no-such-index", index));
      return Result.FAILURE;
    }

    String element = history.get(index - 1);
    log.debug("Recalling from history: {}", element);

    return context.getShell().execute(element);
  }
}
