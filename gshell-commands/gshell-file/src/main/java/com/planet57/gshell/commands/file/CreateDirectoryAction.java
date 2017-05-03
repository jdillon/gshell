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
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.io.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Create a directory.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "mkdir", description = "Create a directory")
public class CreateDirectoryAction
    extends FileCommandActionSupport
{
  @Argument(required = true, description = "The path of the directory create", token = "PATH")
  private String path;

  @Inject
  public CreateDirectoryAction installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    File file = getFileSystem().resolveFile(path);

    new FileAssert(file).exists(false).isFile(false);

    if (!file.mkdirs()) {
      throw new RuntimeException(String.format("Failed to create directory: %s", file));
    }

    return null;
  }
}
