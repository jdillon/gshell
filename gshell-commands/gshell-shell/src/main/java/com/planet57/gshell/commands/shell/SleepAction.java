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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import org.sonatype.goodies.common.Time;

import javax.annotation.Nonnull;

/**
 * Sleep for a period.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "sleep")
public class SleepAction
    extends CommandActionSupport
{
  @Argument(required = true)
  private Time time;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    if (log.isTraceEnabled()) {
      log.trace("Sleeping for {} on thread: {}", time, Thread.currentThread());
    }
    else {
      log.debug("Sleeping for {}", time);
    }

    try {
      time.sleep();
    }
    catch (InterruptedException e) {
      if (log.isTraceEnabled()) {
        log.trace("Sleep was interrupted", e);
      }
      else {
        log.debug("Sleep was interrupted");
      }
      return Result.FAILURE;
    }

    log.debug("Awake now");

    return Result.SUCCESS;
  }
}
