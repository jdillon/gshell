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
import javax.inject.Inject;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.io.FileSystemAccess;
import com.planet57.gshell.util.io.FileAssert;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays the current directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "pwd")
public class CurrentDirectoryCommand
    extends CommandActionSupport
{
  private final FileSystemAccess fileSystem;

  @Inject
  public CurrentDirectoryCommand(final FileSystemAccess fileSystem) {
    this.fileSystem = checkNotNull(fileSystem);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    File dir = fileSystem.getUserDir();
    new FileAssert(dir).exists().isDirectory();

    io.println(dir.getPath());

    return Result.SUCCESS;
  }
}
