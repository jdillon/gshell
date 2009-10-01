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

import org.apache.gshell.History;
import org.apache.gshell.cli.Argument;
import org.apache.gshell.cli.Option;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.io.IO;
import org.codehaus.plexus.component.annotations.Component;

import java.util.List;

/**
 * Display history.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Component(role=HistoryCommand.class)
public class HistoryCommand
    extends CommandActionSupport
{
    @Option(name="-c", aliases={"--clear"})
    private boolean clear = false;

    @Option(name="-p", aliases={"--purge"})
    private boolean purge = false;
    
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
            // Display all elements
            List<String> elements = history.elements();
            int i = 0;
            for (String element : elements) {
                renderElement(io, i, element);
                i++;
            }
        }
        else {
            // Display elements in range
            int n = Integer.parseInt(range);
            List<String> elements = history.elements();
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
        io.info("  @|bold {}| {}", index, element);
    }
}