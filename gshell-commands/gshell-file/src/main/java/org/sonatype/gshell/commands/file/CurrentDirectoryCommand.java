/*
 * Copyright (c) 2009-2011 the original author or authors.
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

import javax.inject.Inject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.file.FileSystemAccess;
import org.sonatype.gshell.util.FileAssert;

import java.io.File;

/**
 * Displays the current directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="pwd")
public class CurrentDirectoryCommand
    extends CommandActionSupport
{
    private final FileSystemAccess fileSystem;

    @Inject
    public CurrentDirectoryCommand(final FileSystemAccess fileSystem) {
        assert fileSystem != null;
        this.fileSystem = fileSystem;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        File dir = fileSystem.getUserDir();
        new FileAssert(dir).exists().isDirectory();
        
        io.println(dir.getPath());

        return Result.SUCCESS;
    }
}