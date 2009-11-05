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

package org.sonatype.gshell.core.commands;

import org.sonatype.gshell.History;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.cli.Option;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.core.command.CommandActionSupport;

import java.util.List;

/**
 * Display history.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Command
public class HistoryCommand
    extends CommandActionSupport
{
    @Option(name="-c", aliases={"--clear"})
    private boolean clear;

    @Option(name="-p", aliases={"--purge"})
    private boolean purge;
    
    @Argument()
    private String range;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        History history = context.getShell().getHistory();

        if (clear) {
            history.clear();
            log.debug("History clearend");
        }

        if (purge) {
            history.purge();
            log.debug("History purged");
        }

        return displayRange(context);
    }

    private Object displayRange(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        History history = context.getShell().getHistory();

        if (range == null) {
            // Display all items
            List<String> elements = history.items();
            int i = 0;
            for (String element : elements) {
                renderElement(io, i, element);
                i++;
            }
        }
        else {
            // Display items in range
            int n = Integer.parseInt(range);
            List<String> elements = history.items();
            if (n > elements.size()) {
                n = 0;
            }
            int i = elements.size() - n;
            while (i < elements.size()) {
                renderElement(io, i, elements.get(i));
                i++;
            }
        }

        return Result.SUCCESS;
    }

    private void renderElement(final IO io, final int i, final String element) {
        String index = String.format("%3d", i);
        io.info("  @|bold {}|@ {}", index, element);
    }
}