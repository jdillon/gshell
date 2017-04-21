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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.file.FileSystemAccess;
import com.planet57.gshell.util.io.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.jline.reader.Completer;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * List the contents of a file or directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "ls")
public class ListDirectoryCommand
  extends CommandActionSupport {
  private final FileSystemAccess fileSystem;

  @Argument
  private String path;

  @Option(name = "l", longName = "long")
  private boolean longList;

  @Option(name = "a", longName = "all")
  private boolean includeHidden;

  @Option(name = "r", longName = "recursive")
  private boolean recursive;

  @Inject
  public ListDirectoryCommand(final FileSystemAccess fileSystem) {
    this.fileSystem = checkNotNull(fileSystem);
  }

  @Inject
  public ListDirectoryCommand installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    File file = fileSystem.resolveFile(path);

    new FileAssert(file).exists();

    if (file.isDirectory()) {
      listChildren(io, file);
    }
    else {
      io.println(file.getPath());
    }

    return Result.SUCCESS;
  }

  private void listChildren(final IO io, final File dir) throws Exception {
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
      if (fileSystem.hasChildren(file)) {
        if (recursive) {
          dirs.add(file);
        }
      }

      names.add(render(file));
    }

    if (longList) {
      for (CharSequence name : names) {
        io.out.println(name);
      }
    } else {
      toColumn(io.getTerminal(), io.out, names.stream(), true);
    }

    if (!dirs.isEmpty()) {
      for (File subDir : dirs) {
        io.out.println();
        io.out.print(subDir.getName());
        io.out.print(":");
        listChildren(io, subDir);
      }
    }
  }

  // TODO: sort out some sort of scheme where this can be made configurable

  private String render(final File file) {
    String name = file.getName();

    AttributedStringBuilder buff = new AttributedStringBuilder();
    if (file.isDirectory()) {
      buff.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
      buff.append(name);
      buff.append(File.separator);
      buff.style(AttributedStyle.DEFAULT);
    }
    else if (file.canExecute()) {
      buff.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
      buff.append(name);
      buff.style(AttributedStyle.DEFAULT);
    }
    else if (file.isHidden()) {
      buff.style(AttributedStyle.DEFAULT.faint());
      buff.append(name);
      buff.style(AttributedStyle.DEFAULT);
    }
    else {
      return name;
    }

    return buff.toAnsi();
  }

  // TODO: move to helper

  // Adapted from: https://github.com/apache/felix/blob/trunk/gogo/jline/src/main/java/org/apache/felix/gogo/jline/Posix.java

  private void toColumn(final Terminal terminal, final PrintWriter out, final Stream<String> values, final boolean horizontal) {
    int width = terminal.getWidth();
    List<AttributedString> strings = values.map(AttributedString::fromAnsi).collect(Collectors.toList());

    if (!strings.isEmpty()) {
      int max = strings.stream().mapToInt(AttributedString::columnLength).max().getAsInt();
      int c = Math.max(1, width / max);
      while (c > 1 && c * max + (c - 1) >= width) {
        c--;
      }

      int columns = c;
      int lines = (strings.size() + columns - 1) / columns;
      IntBinaryOperator index;

      if (horizontal) {
        index = (i, j) -> i * columns + j;
      }
      else {
        index = (i, j) -> j * lines + i;
      }

      AttributedStringBuilder buff = new AttributedStringBuilder();
      for (int i = 0; i < lines; i++) {
        for (int j = 0; j < columns; j++) {
          int idx = index.applyAsInt(i, j);
          if (idx < strings.size()) {
            AttributedString str = strings.get(idx);
            boolean hasRightItem = j < columns - 1 && index.applyAsInt(i, j + 1) < strings.size();
            buff.append(str);
            if (hasRightItem) {
              for (int k = 0; k <= max - str.length(); k++) {
                buff.append(' ');
              }
            }
          }
        }
        buff.append('\n');
      }

      out.print(buff.toAnsi(terminal));
    }
  }
}
