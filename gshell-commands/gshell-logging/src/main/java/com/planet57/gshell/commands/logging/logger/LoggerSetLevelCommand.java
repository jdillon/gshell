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
package com.planet57.gshell.commands.logging.logger;

import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.logging.Level;
import com.planet57.gshell.logging.Logger;
import com.planet57.gshell.logging.LoggingSystem;
import com.planet57.gshell.util.cli2.Argument;
import jline.console.completer.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Set the level of a logger.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "logging/logger/set")
public class LoggerSetLevelCommand
    extends CommandActionSupport
{
  private final LoggingSystem logging;

  @Argument(index = 0, required = true)
  private String loggerName;

  @Argument(index = 1, required = true)
  private String levelName;

  @Inject
  public LoggerSetLevelCommand(final LoggingSystem logging) {
    this.logging = checkNotNull(logging);
  }

  @Inject
  public void installCompleters(final @Named("logger-name") Completer c1, final @Named("level-name") Completer c2) {
    checkNotNull(c1);
    checkNotNull(c2);
    setCompleters(c1, c2, null);
  }

  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);

    Logger logger = logging.getLogger(loggerName);
    Level level = logging.getLevel(levelName);
    logger.setLevel(level);

    log.debug("Set logger {} level to: {}", logger, level);

    return Result.SUCCESS;
  }
}
