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

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ListIterator;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.jline.reader.History;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import javax.annotation.Nonnull;

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

  @Option(name = "s", longName = "save")
  private boolean save;

  @Option(name = "t", longName = "timestamps")
  private boolean timestamps;

  @Argument()
  private Integer last;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    History history = context.getShell().getHistory();

    if (purge) {
      history.purge();
      log.debug("History purged");
      return Result.SUCCESS;
    }
    else if (save) {
      history.save();
      log.debug("History saved");
      return Result.SUCCESS;
    }

    return displayEntries(context);
  }

  private Object displayEntries(final CommandContext context) throws Exception {
    IO io = context.getIo();
    History history = context.getShell().getHistory();

    log.debug("History size: {}", history.size());

    int i = 0;
    log.debug("Starting with entry: {}", i);

    ListIterator<History.Entry> entries = history.iterator(i);
    while (entries.hasNext()) {
      renderEntry(io, entries.next());
    }

    return Result.SUCCESS;
  }

  private void renderEntry(final IO io, final History.Entry entry) {
    String index = String.format("%3d", entry.index() + 1);

    AttributedStringBuilder buff = new AttributedStringBuilder();

    if (timestamps) {
      LocalTime lt = LocalTime.from(entry.time().atZone(ZoneId.systemDefault())).truncatedTo(ChronoUnit.SECONDS);
      DateTimeFormatter.ISO_LOCAL_TIME.formatTo(lt, buff);
      buff.append(" ");
    }

    buff.style(AttributedStyle.BOLD);
    buff.append(index);
    buff.style(AttributedStyle.DEFAULT);
    buff.append("  ").append(entry.line());

    io.println(buff.toAnsi(io.getTerminal()));
  }
}
