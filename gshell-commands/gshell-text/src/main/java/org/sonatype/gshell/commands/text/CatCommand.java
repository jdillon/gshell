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
package org.sonatype.gshell.commands.text;

import org.apache.commons.vfs.FileContentInfo;
import org.apache.commons.vfs.FileObject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.commands.vfs.VfsCommandSupport;
import org.sonatype.gshell.util.io.Closer;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.vfs.FileObjectAssert;
import org.sonatype.gshell.vfs.FileObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Displays the contents of a file.
 *
 * @since 2.0
 */
@Command(name="cat")
public class CatCommand
    extends VfsCommandSupport
{
    @Option(name="n")
    private boolean displayLineNumbers;

    @Argument(required=true)
    private String path;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        //
        // TODO: Support multi-path cat, and the special '-' token (which is the default if no paths are given)
        //

        FileObject file = resolveFile(context, path);

        new FileObjectAssert(file).exists();
        ensureFileHasContent(file);

        org.apache.commons.vfs.FileContent content = file.getContent();
        FileContentInfo info = content.getContentInfo();
        log.debug("Content type: {}", info.getContentType());
        log.debug("Content encoding: {}", info.getContentEncoding());

        //
        // TODO: Only cat files which we think are text, or warn if its not, allow flag to force
        //

        log.debug("Displaying file: {}", file.getName());

        BufferedReader reader = new BufferedReader(new InputStreamReader(content.getInputStream()));
        try {
            cat(reader, io);
        }
        finally {
            Closer.close(reader);
        }

        FileObjects.close(file);

        return Result.SUCCESS;
    }

    private void cat(final BufferedReader reader, final IO io) throws IOException {
        String line;
        int lineno = 1;

        while ((line = reader.readLine()) != null) {
            if (displayLineNumbers) {
                io.out.print(String.format("%6d  ", lineno++));
            }
            io.out.println(line);
        }
    }
}