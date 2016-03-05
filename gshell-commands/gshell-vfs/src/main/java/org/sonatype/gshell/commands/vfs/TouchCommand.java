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
package org.sonatype.gshell.commands.vfs;

import org.apache.commons.vfs.FileObject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.vfs.FileObjects;

/**
 * Sets the last-modified time of a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="vfs/touch")
public class TouchCommand
    extends VfsCommandSupport
{
    @Argument(required=true)
    private String path;

    // TODO: Add options similar to UNIX touch (like -r FILE) see man page for more details.

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        FileObject file = resolveFile(context, path);

        try {
            if (!file.exists()) {
                file.createFile();
            }

            file.getContent().setLastModifiedTime(System.currentTimeMillis());
        }
        finally {
            FileObjects.close(file);
        }

        return Result.SUCCESS;
    }
}