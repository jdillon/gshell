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

package org.sonatype.gshell.commands.vfs;

import org.apache.commons.vfs.FileObject;
import org.sonatype.gshell.vars.VariableNames;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.vfs.FileObjectAssert;
import org.sonatype.gshell.vfs.FileObjects;

/**
 * Changes the current directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="cd")
public class ChangeDirectoryCommand
    extends VfsCommandSupport
    implements VariableNames
{
    @Argument
    private String path;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (path == null) {
            path = context.getVariables().get(SHELL_USER_HOME, String.class);
        }

        FileObject file = resolveFile(context, path);

        new FileObjectAssert(file).exists().isDirectory();

        setCurrentDirectory(context, file);

        FileObjects.close(file);
        
        return Result.SUCCESS;
    }
}