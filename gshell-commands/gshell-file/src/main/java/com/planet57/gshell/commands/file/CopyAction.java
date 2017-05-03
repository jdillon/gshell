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
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.apache.commons.io.FileUtils;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Copy file or directory
 *
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 * @since 2.6.3
 */
@Command(name = "cp", description = "Copy files")
public class CopyAction
    extends FileCommandActionSupport
{
  @Argument(required = true, index = 0, description = "The path to the file or directory to copy", token = "SOURCE")
  private String source;

  @Argument(required = true, index = 1, description = "The path to the target file or directory", token = "TARGET")
  private String target;

  @Option(name = "r", longName = "recursive")
  private boolean recursive;

  @Inject
  public CopyAction installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    // Add completer for source and target
    setCompleters(c1, c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    File sourceFile = getFileSystem().resolveFile(source);
    File targetFile = getFileSystem().resolveFile(target);

    if (sourceFile.isDirectory()) {
      // for cp -r /tmp/foo /home : we must create first the directory /home/foo
      targetFile = new File(targetFile, sourceFile.getName());
      if (!targetFile.exists()) {
        targetFile.mkdirs();
      }
      if (recursive) {
        FileUtils.copyDirectory(sourceFile, targetFile);
      }
      else {
        throw new RuntimeException("--recursive not specified; omitting directory: " + sourceFile);
      }
    }
    else {
      if (targetFile.isDirectory()) {
        FileUtils.copyFileToDirectory(sourceFile, targetFile);
      }
      else {
        FileUtils.copyFile(sourceFile, targetFile);
      }
    }

    return null;
  }
}
