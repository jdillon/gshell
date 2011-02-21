/**
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

import jline.console.ConsoleReader;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSystemException;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.vfs.FileObjects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * List the contents of a file or directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="vfs/ls")
public class ListDirectoryCommand
    extends VfsCommandSupport
{
    @Argument
    private String path;

    @Option(name="l", longName="long")
    private boolean longList;

    @Option(name="a", longName="all")
    private boolean includeHidden;

    @Option(name="r", longName="recursive")
    private boolean recursive;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        FileObject file;
        if (path != null) {
            file = resolveFile(context, path);
        }
        else {
            file = getCurrentDirectory(context);
        }

        if (file.getType().hasChildren()) {
            listChildren(io, file);
        }
        else {
            io.println(file.getName().getPath());
        }

        FileObjects.close(file);
        
        return Result.SUCCESS;
    }

    private void listChildren(final IO io, final FileObject dir) throws Exception {
        assert io != null;
        assert dir != null;

        FileObject[] files;

        if (includeHidden) {
            files = dir.getChildren();
        }
        else {
            FileFilter filter = new FileFilter() {
                public boolean accept(final FileSelectInfo selection) {
                    assert selection != null;

                    try {
                        return !selection.getFile().isHidden();
                    }
                    catch (FileSystemException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            files = dir.findFiles(new FileFilterSelector(filter));
        }

        ConsoleReader reader = new ConsoleReader(
            io.streams.in,
            io.out,
            io.getTerminal());

        reader.setPaginationEnabled(false);

        List<String> names = new ArrayList<String>(files.length);
        List<FileObject> dirs = new LinkedList<FileObject>();

        for (FileObject file : files) {
            String fileName = file.getName().getBaseName();

            if (FileObjects.hasChildren(file)) {
                fileName += FileName.SEPARATOR;

                if (recursive) {
                    dirs.add(file);
                }
            }

            names.add(fileName);

            file.close();
        }

        if (longList) {
            for (String name : names) {
                io.out.println(name);
            }
        }
        else {
            reader.printColumns(names);
        }

        if (!dirs.isEmpty()) {
            for (FileObject subdir : dirs) {
                io.out.println();
                io.out.print(subdir.getName().getBaseName());
                io.out.print(":");
                listChildren(io, subdir);
            }
        }

        dir.close();
    }
}