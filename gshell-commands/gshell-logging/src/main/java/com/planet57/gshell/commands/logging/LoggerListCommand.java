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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.util.cli2.Option;

/**
 * List loggers.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "logging/logger/list")
public class LoggerListCommand
  extends LoggingComponenSupport
{
  @Option(name = "n", longName = "name")
  private String nameQuery;

  @Option(name = "l", longName = "level")
  private String levelQuery;

  @Option(name = "a", longName = "all")
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
          io.println("%s: %s", logger.getName(), logger.getLevel());
        }
      }
    }

    return Result.SUCCESS;
  }
}
