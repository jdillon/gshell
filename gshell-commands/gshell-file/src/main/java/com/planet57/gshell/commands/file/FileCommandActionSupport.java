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

import static com.google.common.base.Preconditions.checkState;

import javax.inject.Inject;

import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.io.FileSystemAccess;

/**
 * Support file {@code file} actions.
 *
 * @since 3.0
 */
public abstract class FileCommandActionSupport
    extends CommandActionSupport
{
  private FileSystemAccess fileSystem;

  @Inject
  public void setFileSystem(final FileSystemAccess fileSystem) {
    this.fileSystem = fileSystem;
  }

  protected FileSystemAccess getFileSystem() {
    checkState(fileSystem != null);
    return fileSystem;
  }
}
