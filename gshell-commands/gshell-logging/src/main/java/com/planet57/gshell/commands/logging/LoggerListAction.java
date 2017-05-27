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
package com.planet57.gshell.commands.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.util.cli2.Option;

/**
 * List loggers.
 *
 * @since 2.5
 */
@Command(name = "logging/loggers", description = "List loggers")
public class LoggerListAction
  extends LoggingCommandActionSupport
{
  @Nullable
  @Option(name = "n", longName = "name", description = "Include loggers matching NAME", token = "NAME")
  private String nameQuery;

  @Nullable
  @Option(name = "l", longName = "level", description = "Include loggers matching LEVEL", token = "LEVEL")
  private String levelQuery;

  @Option(name = "a", longName = "all", description = "Include loggers with no level directly configured")
  private boolean all;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    List<String> names = new ArrayList<>();
    names.addAll(getLogging().getLoggerNames());
    Collections.sort(names);

    for (String name : names) {
      if (nameQuery == null || name.contains(nameQuery)) {
        LoggerComponent logger = getLogging().getLogger(name);
        if (all || logger.getLevel() != null &&
            (levelQuery == null || logger.getLevel().toString().contains(levelQuery.toUpperCase()))) {
          io.format("%s: %s%n", logger.getName(), logger.getLevel());
        }
      }
    }

    return null;
  }
}
