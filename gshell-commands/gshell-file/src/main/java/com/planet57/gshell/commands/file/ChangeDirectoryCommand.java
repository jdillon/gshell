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
package com.planet57.gshell.commands.file;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.support.CommandActionSupport;
import com.planet57.gshell.file.FileSystemAccess;
import com.planet57.gshell.util.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.variables.Variables;
import jline.console.completer.Completer;

import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;

/**
 * Changes the current directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="cd")
public class ChangeDirectoryCommand
    extends CommandActionSupport
{
    private final FileSystemAccess fileSystem;

    @Option(name="v", longName="verbose")
    private boolean verbose;

    @Argument
    private String path;

    @Inject
    public ChangeDirectoryCommand(final FileSystemAccess fileSystem) {
        assert fileSystem != null;
        this.fileSystem = fileSystem;
    }

    @Inject
    public ChangeDirectoryCommand installCompleters(final @Named("file-name") Completer c1) {
        assert c1 != null;
        setCompleters(c1, null);
        return this;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        Variables vars = context.getVariables();

        File file;
        if (path == null) {
            file = fileSystem.getUserHomeDir();
        }
        else {
            file = fileSystem.resolveFile(path);
        }

        new FileAssert(file).exists().isDirectory();

        vars.set(SHELL_USER_DIR, file.getPath());
        if (verbose) {
            io.println(file.getPath());
        }

        return Result.SUCCESS;
    }
}