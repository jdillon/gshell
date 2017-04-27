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
import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;

/**
 * Changes the current directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "cd")
public class ChangeDirectoryAction
    extends FileCommandActionSupport
{
  @Option(name = "v", longName = "verbose")
  private boolean verbose;

  @Nullable
  @Argument
  private String path;

  @Inject
  public ChangeDirectoryAction installCompleters(final @Named("directory-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    Variables vars = context.getVariables();

    File file;
    if (path == null) {
      file = getFileSystem().getUserHomeDir();
    }
    else {
      file = getFileSystem().resolveFile(path);
    }

    new FileAssert(file).exists().isDirectory();

    vars.set(SHELL_USER_DIR, file.getPath());
    if (verbose) {
      io.out.println(file.getPath());
    }

    return Result.SUCCESS;
  }
}
