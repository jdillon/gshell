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
import org.apache.maven.shell.Command;
import org.apache.maven.shell.CommandContext;
import org.apache.maven.shell.CommandSupport;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;

import java.util.List;

/**
 * Display history.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="history", instantiationStrategy="per-lookup")
public class HistoryCommand
    extends CommandSupport
{
    // TODO: Support displaying a range of history
    
    // TODO: Add clear and recall support

    public String getName() {
        return "history";
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        // HACK: Get at the shell's history from our variables
        History history = context.getVariables().get(Shell.SHELL_INTERNAL + History.class.getName(), History.class);
        assert history != null;

        // noinspection unchecked
        List<String> elements = history.getHistoryList();

        int i = 0;
        for (String element : elements) {
            String index = String.format("%3d", i);
            io.info("  @|bold {}| {}", index, element);
            i++;
        }

        return Result.SUCCESS;
    }
}