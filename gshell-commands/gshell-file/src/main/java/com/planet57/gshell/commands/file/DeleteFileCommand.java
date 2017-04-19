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

import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.file.FileSystemAccess;
import com.planet57.gshell.util.io.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import jline.console.completer.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Remove a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "rm")
public class DeleteFileCommand
    extends CommandActionSupport
{
  private final FileSystemAccess fileSystem;

  @Argument(required = true)
  private String path;

  @Inject
  public DeleteFileCommand(final FileSystemAccess fileSystem) {
    this.fileSystem = checkNotNull(fileSystem);
  }

  @Inject
  public DeleteFileCommand installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);
    IO io = context.getIo();

    File file;
    if (path == null) {
      file = fileSystem.getUserHomeDir();
    }
    else {
      file = fileSystem.resolveFile(path);
    }

    new FileAssert(file).exists().isFile();

    if (!file.delete()) {
      io.error(getMessages().format("error.delete-failed", file));
      return Result.FAILURE;
    }

    // TODO: Add recursive delete

    return Result.SUCCESS;
  }
}
