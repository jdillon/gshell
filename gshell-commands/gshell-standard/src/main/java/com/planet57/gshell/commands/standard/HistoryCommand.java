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

import java.util.ListIterator;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.jline.reader.History;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display history.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "history")
public class HistoryCommand
    extends CommandActionSupport
{
  @Option(name = "p", longName = "purge")
  private boolean purge;

  @Argument()
  private Integer last;

  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);

    History history = context.getShell().getHistory();

    if (purge) {
      history.purge();
      log.debug("History purged");
    }

    return displayEntries(context);
  }

  private Object displayEntries(final CommandContext context) throws Exception {
    IO io = context.getIo();
    History history = context.getShell().getHistory();

    log.debug("History size: {}", history.size());

    int i = 0;
    if (last != null) {
      i = history.size() - last;
      if (i < 0) {
        i = 0;
      }
    }

    log.debug("Starting with entry: {}", i);

    ListIterator<History.Entry> entries = history.iterator(i);
    while (entries.hasNext()) {
      History.Entry entry = entries.next();
      renderElement(io, entry.index(), entry.line());
    }

    return Result.SUCCESS;
  }

  private void renderElement(final IO io, final int i, final CharSequence element) {
    String index = String.format("%3d", i + 1);
    io.println("  @|bold %s|@ {}", index, element);
  }
}
