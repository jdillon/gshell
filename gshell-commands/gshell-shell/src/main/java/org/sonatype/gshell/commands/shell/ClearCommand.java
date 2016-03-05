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
package org.sonatype.gshell.commands.shell;

import org.fusesource.jansi.Ansi;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;

/**
 * Clear the screen.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="clear")
public class ClearCommand
    extends CommandActionSupport
{
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        io.out.print(Ansi.ansi().eraseScreen(Ansi.Erase.ALL));
        io.out.flush();
        
        return Result.SUCCESS;
    }
}