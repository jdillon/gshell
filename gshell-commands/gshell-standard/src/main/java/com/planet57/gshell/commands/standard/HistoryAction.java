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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Option;
import org.jline.reader.History;
import org.jline.style.StyleFactory;
import org.jline.style.Styler;
import org.jline.utils.AttributedStringBuilder;

import javax.annotation.Nonnull;

/**
 * Display history.
 *
 * @since 2.5
 */
@Command(name = "history", description = "Display history")
public class HistoryAction
    extends CommandActionSupport
{
  @Option(name = "p", longName = "purge", description = "Purge the shell history")
  private boolean purge;

  @Option(name = "s", longName = "save", description = "Save shell history")
  private boolean save;

  @Option(name = "t", longName = "timestamps", description = "Display timestamps")
  private boolean timestamps;

  private final StyleFactory styles = Styler.factory("command.history");

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    History history = context.getShell().getHistory();

    if (purge) {
      history.purge();
      log.debug("History purged");
    }
    else if (save) {
      history.save();
      log.debug("History saved");
    }
    else {
      displayEntries(context.getIo(), history);
    }

    return null;
  }

  private void displayEntries(final IO io, final History history) {
    log.debug("History size: {}", history.size());

    history.forEach(entry -> renderEntry(io, entry));
  }

  private void renderEntry(final IO io, final History.Entry entry) {
    AttributedStringBuilder buff = new AttributedStringBuilder();

    if (timestamps) {
      LocalTime lt = LocalTime.from(entry.time().atZone(ZoneId.systemDefault())).truncatedTo(ChronoUnit.SECONDS);
      DateTimeFormatter.ISO_LOCAL_TIME.formatTo(lt, buff);
      buff.append(" ");
    }

    int index = entry.index() + 1;
    buff.append(styles.style(".index:-bold", "%3d  ", index))
        .append(entry.line());

    io.println(buff.toAnsi(io.terminal));
  }
}
