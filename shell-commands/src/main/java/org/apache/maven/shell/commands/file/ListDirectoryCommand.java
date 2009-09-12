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

import jline.ConsoleReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.IO;

/**
 * List the contents of a file or directory.
 *
 * @version $Rev$ $Date$
 */
public class ListDirectoryCommand
    extends CommandSupport
{
    @Argument
    private String path;

    @Option(name="-l", aliases={"--long"})
    private boolean longList;

    @Option(name="-a", aliases={"--all"})
    private boolean includeHidden;

    @Option(name="-r", aliases={"--recursive"})
    private boolean recursive;

    public String getName() {
        return "ls";
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        /*
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
            io.info(file.getName().getPath());
        }

        FileObjects.close(file);
        */

        return Result.SUCCESS;
    }

    /*
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
        
        ConsoleReader reader = io.createConsoleReader();
        reader.setUsePagination(false);

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
    */
}