/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.commands.standard;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.notification.ExitNotification;
import org.sonatype.gshell.util.cli2.Argument;

/**
 * Exit the current shell.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="exit")
public class ExitCommand
    extends CommandActionSupport
{
    @Argument
    private int exitCode = 0;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        log.info("Exiting w/code: {}", exitCode);

        // Do not call System.exit(), ask the shell to exit instead.
        throw new ExitNotification(exitCode);
    }
}