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

package org.sonatype.gshell.commands.file;

import com.google.inject.Inject;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.completer.FileNameCompleter;
import org.sonatype.gshell.util.FileAssert;
import org.sonatype.gshell.util.cli.Argument;

import java.io.File;

/**
 * Remove a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="rm")
public class DeleteFileCommand
    extends FileCommandSupport
{
    @Argument
    private String path;

    @Inject
    public DeleteFileCommand installCompleters(final FileNameCompleter c1) {
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
            file = getUserHomeDir(context);
        }
        else {
            file = resolveFile(context, path);
        }

        new FileAssert(file).exists().isFile();

        if (!file.delete()) {
            io.error(getMessages().format("error.delete-failed", file));
            return Result.FAILURE;
        }

        // TODO: Add recursive delete

        return Result.SUCCESS;
    }
}