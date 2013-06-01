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
package org.sonatype.gshell.commands.vfs;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.vfs.FileObjectAssert;
import org.sonatype.gshell.vfs.FileObjects;

/**
 * Copies a file or directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="vfs/cp")
public class CopyCommand
    extends VfsCommandSupport
{
    @Argument(index=0, required=true)
    private String sourcePath;

    @Argument(index=1, required=true)
    private String targetPath;

    // TODO: Add --recursive suport

    // TODO: Add --verbose support
    
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        FileObject source = resolveFile(context, sourcePath);
        FileObject target = resolveFile(context, targetPath);

        new FileObjectAssert(source).exists();

        // TODO: Validate more

        if (target.exists() && target.getType().hasChildren()) {
            target = target.resolveFile(source.getName().getBaseName());
        }

        log.info("Copying {} -> {}", source, target);

        target.copyFrom(source, Selectors.SELECT_ALL);

        FileObjects.close(source, target);
        
        return Result.SUCCESS;
    }
}