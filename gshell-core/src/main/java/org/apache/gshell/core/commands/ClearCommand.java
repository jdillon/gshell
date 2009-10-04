/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.core.commands;

import jline.console.ConsoleReader;
import org.apache.gshell.ansi.Ansi;
import org.apache.gshell.command.Command;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.io.IO;

/**
 * Clear the terminal screen.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Command
public class ClearCommand
    extends CommandActionSupport
{
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        // We can only clear the screen if ANSI is enabled, so complain and fail otherwise
        if (!Ansi.isEnabled()) {
            io.error(getMessages().format("error.ansi-disabled"));
            return Result.FAILURE;
        }

        ConsoleReader reader = io.createConsoleReader();
        reader.clearScreen();

        return Result.SUCCESS;
    }
}
