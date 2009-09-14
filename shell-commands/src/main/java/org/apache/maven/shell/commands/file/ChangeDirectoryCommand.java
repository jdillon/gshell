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

package org.apache.maven.shell.commands.file;

import jline.Completor;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.File;
import java.util.List;

/**
 * Changes the current directory.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="cd", instantiationStrategy="per-lookup")
public class ChangeDirectoryCommand
    extends FileCommandSupport
{
    @Requirement(role=Completor.class, hints={"file-name"})
    private List<Completor> completers;

    @Argument
    private String path;

    @Override
    public Completor[] getCompleters() {
        assert completers != null;

        return new Completor[] {
            new AggregateCompleter(completers),
            null
        };
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        Variables vars = context.getVariables();

        File file;
        if (path == null) {
            file = getUserHomeDir(context);
        }
        else {
            file = resolveFile(context, path);
        }

        if (!file.exists()) {
            io.error("File not found: {}", file);
            return Result.FAILURE;
        }
        else if (!file.isDirectory()) {
            io.error("Not a directory: {}", file);
            return Result.FAILURE;
        }

        vars.set(MVNSH_USER_DIR, file.getPath());
        io.info(file.getPath());

        return Result.SUCCESS;
    }
}