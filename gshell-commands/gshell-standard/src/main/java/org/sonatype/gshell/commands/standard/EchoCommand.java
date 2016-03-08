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
package org.sonatype.gshell.commands.standard;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import java.util.Iterator;
import java.util.List;

/**
 * Print all arguments to the commands standard output.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="echo")
public class EchoCommand
    extends CommandActionSupport
{
    @Option(name = "n", optionalArg = true)
    private boolean noTrailingNewline;

    @Argument()
    private List<String> args;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (args != null && !args.isEmpty()) {
            Iterator iter = args.iterator();

            while (iter.hasNext()) {
                io.out.print(iter.next());
                if (iter.hasNext()) {
                    io.out.print(" ");
                }
            }
        }

        if (!noTrailingNewline) {
            io.out.println();
        }

        return Result.SUCCESS;
    }
}