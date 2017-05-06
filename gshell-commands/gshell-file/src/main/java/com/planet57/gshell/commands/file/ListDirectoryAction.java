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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.io.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.io.FileSystemAccess;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.util.jline.Complete;
import com.planet57.gshell.util.jline.TerminalHelper;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * List the contents of a file or directory.
 *
 * @since 2.0
 */
@Command(name = "ls", description = "List the contents of a file or directory")
public class ListDirectoryAction
  extends FileCommandActionSupport
{
  @Nullable
  @Argument(description = "The file or directory path to list.", token = "PATH")
  @Complete("file-name")
  private String path;

  @Option(name = "l", longName = "long", description = "List in long format")
  private boolean longList;

  @Option(name = "a", longName = "all", description = "Include hidden files")
  private boolean includeHidden;

  @Option(name = "r", longName = "recursive", description = "List the contents of directories recursively")
  private boolean recursive;

  @Inject
  public ListDirectoryAction installCompleters(@Named("file-name") final Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    FileSystemAccess fs = getFileSystem();

    File file = fs.resolveFile(path);

    new FileAssert(file).exists();

    if (file.isDirectory()) {
      listChildren(fs, io, file);
    }
    else {
      io.println(file.getPath());
    }

    return null;
  }

  private void listChildren(final FileSystemAccess fs, final IO io, final File dir) throws Exception {
    File[] files;

    if (includeHidden) {
      files = dir.listFiles();
    }
    else {
      files = dir.listFiles(file -> {
        assert file != null;
        return !file.isHidden();
      });
    }
    assert files != null;

    List<String> names = new ArrayList<>(files.length);
    List<File> dirs = new LinkedList<>();

    for (File file : files) {
      if (fs.hasChildren(file)) {
        if (recursive) {
          dirs.add(file);
        }
      }

      names.add(render(file));
    }

    if (longList) {
      for (CharSequence name : names) {
        io.println(name);
      }
    }
    else {
      TerminalHelper.printColumns(io.terminal, io.out, names.stream(), true);
    }

    if (!dirs.isEmpty()) {
      for (File subDir : dirs) {
        io.format("%n%s:", subDir.getName());
        listChildren(fs, io, subDir);
      }
    }
  }

  private static String render(final File file) {
    String name = file.getName();

    PrintBuffer buff = new PrintBuffer();
    if (file.isDirectory()) {
      buff.format("@|blue %s%s|@", name, File.separator);
    }
    else if (file.canExecute()) {
      buff.format("@|green %s|@", name);
    }
    else if (file.isHidden()) {
      buff.format("@|intensity_faint %s|@", name);
    }
    else {
      // not styled
      return name;
    }

    return buff.toString();
  }
}
