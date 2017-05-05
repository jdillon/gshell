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
import org.apache.felix.service.command.Process;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import javax.annotation.Nonnull;

/**
 * Clear the screen.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "clear", description = "Clear the screen")
public class ClearAction
    extends CommandActionSupport
{
  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Terminal terminal = context.getIo().terminal;

    Process process = Process.Utils.current();
    if (process.isTty(1)) {
      terminal.puts(InfoCmp.Capability.clear_screen);
      terminal.flush();
    }

    return null;
  }
}
