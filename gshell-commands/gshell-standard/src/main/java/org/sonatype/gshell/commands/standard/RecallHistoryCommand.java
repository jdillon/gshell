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
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.shell.History;
import org.sonatype.gshell.util.cli2.Argument;

/**
 * Recall history.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="recall")
public class RecallHistoryCommand
    extends CommandActionSupport
{
    @Argument(required = true)
    private int index;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        History history = context.getShell().getHistory();

        if (index < 1 || index > history.size()) {
            io.error(getMessages().format("error.no-such-index", index));
            return Result.FAILURE;
        }

        CharSequence element = history.get(index - 1);
        log.debug("Recalling from history: {}", element);

        // Replace the last item (which was this command, with the recalled bits)
        history.replace(element);
        
        return context.getShell().execute(element);
    }
}