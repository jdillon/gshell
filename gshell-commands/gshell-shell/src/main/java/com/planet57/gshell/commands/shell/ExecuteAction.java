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

import java.util.List;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Execute system processes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "exec")
public class ExecuteAction
    extends CommandActionSupport
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Argument(required = true)
  private List<String> args;

  // TODO: Support setting the process directory and environment muck

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    ProcessBuilder builder = new ProcessBuilder(args);

    log.debug("Executing: {}", builder.command());

    Process p = builder.start();

    PumpStreamHandler handler = new PumpStreamHandler(io.streams);
    handler.attach(p);
    handler.start();

    log.debug("Waiting for process to exit...");

    int status = p.waitFor();

    log.debug("Process exited w/status: {}", status);

    handler.stop();

    return status;
  }
}
