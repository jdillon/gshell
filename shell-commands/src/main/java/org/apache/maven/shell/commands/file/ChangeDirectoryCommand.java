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

import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.IO;

/**
 * Changes the current directory.
 *
 * @version $Rev$ $Date$
 */
public class ChangeDirectoryCommand
    extends CommandSupport
{
    @Argument
    private String path;

    public String getName() {
        return "cd";
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        /*
        if (path == null) {
            // TODO: May need to ask the Application for this, as it might be different depending on the context (ie. remote user, etc)
            path = System.getProperty("user.home");
        }

        FileObject file = resolveFile(context, path);

        ensureFileExists(file);
        ensureFileHasChildren(file);

        setCurrentDirectory(context, file);

        FileObjects.close(file);
        */

        return Result.SUCCESS;
    }
}