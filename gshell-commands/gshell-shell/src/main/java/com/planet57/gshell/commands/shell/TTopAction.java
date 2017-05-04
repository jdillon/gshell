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

import javax.annotation.Nonnull;

import org.jline.builtins.TTop;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;

/**
 * Thread Top action.
 *
 * @since 3.0
 */
@Command(name = "ttop", description = "Display and update sorted information about threads")
public class TTopAction
    extends CommandActionSupport
{
  // TODO: expose more options; see TTop.ttop() in jline-builtins

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    TTop ttop = new TTop(context.getIo().terminal);
    ttop.run();

    return null;
  }
}
