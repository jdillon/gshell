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

package org.apache.maven.shell.commands.basic;

import jline.History;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;

import java.util.List;

/**
 * Recall history.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="recall", instantiationStrategy="per-lookup")
public class RecallHistoryCommand
    extends CommandSupport
{
    @Argument(required=true)
    private int index;

    private History getHistory(final CommandContext context) {
        assert context != null;
        // HACK: Get at the shell's history from our variables
        History history = context.getVariables().get(Shell.SHELL_INTERNAL + History.class.getName(), History.class);
        if (history == null) {
            throw new Error("History missing in shell variables");
        }
        return history;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        History history = getHistory(context);

        // noinspection unchecked
        List<String> elements = history.getHistoryList();

        if (index < 0 || index > elements.size()) {
            io.error("No such history index: {}", index); // TODO: i18n
            return Result.FAILURE;
        }

        Shell shell = context.getShell();
        return shell.execute(elements.get(index));
    }
}