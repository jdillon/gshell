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
package com.planet57.gshell.file;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import com.planet57.gshell.util.OperatingSystem;
import com.planet57.gshell.variables.Variables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.variables.VariableNames.SHELL_HOME;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_HOME;

/**
 * {@link FileSystemAccess} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class FileSystemAccessImpl
    implements FileSystemAccess
{
  private final Provider<Variables> variables;

  @Inject
  public FileSystemAccessImpl(final Provider<Variables> variables) {
    this.variables = checkNotNull(variables);
  }

  @Override
  public File resolveDir(final String name) throws IOException {
    checkNotNull(name);
    return variables.get().get(name, File.class).getCanonicalFile();
  }

  @Override
  public File getShellHomeDir() throws IOException {
    return resolveDir(SHELL_HOME);
  }

  @Override
  public File getUserDir() throws IOException {
    return resolveDir(SHELL_USER_DIR);
  }

  @Override
  public File getUserHomeDir() throws IOException {
    return resolveDir(SHELL_USER_HOME);
  }

  @Override
  public File resolveFile(@Nullable File baseDir, @Nullable final String path) throws IOException {
    File userDir = getUserDir();

    if (baseDir == null) {
      baseDir = userDir;
    }

    File file;
    if (path == null) {
      file = baseDir;
    }
    else if (path.startsWith("~")) {
      File userHome = getUserHomeDir();
      file = new File(userHome.getPath() + path.substring(1));
    }
    else {
      file = new File(path);
    }

    // support paths like "<drive>:" and "/" on windows
    if (OperatingSystem.WINDOWS) {
      if (path != null && path.equals("/")) {
        // Get the current canonical path to access drive root
        String tmp = new File(".").getCanonicalPath().substring(0, 2);
        return new File(tmp + "/").getCanonicalFile();
      }

      String tmp = file.getPath();
      if (tmp.length() == 2 && tmp.charAt(1) == ':') {
        // Have to append "/" on windows it seems to get the right root
        return new File(tmp + "/").getCanonicalFile();
      }
    }

    if (!file.isAbsolute()) {
      file = new File(baseDir, file.getPath());
    }

    return file.getCanonicalFile();
  }

  @Override
  public File resolveFile(final String path) throws IOException {
    return resolveFile(null, path);
  }

  @Override
  public boolean hasChildren(final File file) {
    checkNotNull(file);

    if (file.isDirectory()) {
      File[] children = file.listFiles();

      if (children != null && children.length != 0) {
        return true;
      }
    }

    return false;
  }
}
