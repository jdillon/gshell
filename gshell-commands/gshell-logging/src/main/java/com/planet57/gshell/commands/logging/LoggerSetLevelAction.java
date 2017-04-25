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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.jline.reader.Completer;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.logging.LevelComponent;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.util.cli2.Argument;

/**
 * Set the level of a logger.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "logging/logger")
public class LoggerSetLevelAction
  extends LoggingCommandActionSupport
{
  @Argument(index = 0, required = true)
  private String loggerName;

  @Argument(index = 1, required = true)
  private String levelName;

  @Inject
  public void installCompleters(final @Named("logger-name") Completer c1, final @Named("level-name") Completer c2) {
    checkNotNull(c1);
    checkNotNull(c2);
    setCompleters(c1, c2, null);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    LoggerComponent logger = getLogging().getLogger(loggerName);
    LevelComponent level = getLogging().getLevel(levelName);
    logger.setLevel(level);

    log.debug("Set logger {} level to: {}", logger, level);

    return Result.SUCCESS;
  }
}
