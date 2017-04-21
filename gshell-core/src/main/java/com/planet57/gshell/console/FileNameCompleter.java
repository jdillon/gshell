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
package com.planet57.gshell.console;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_HOME;

/**
 * {@link Completer} for file names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named("file-name")
@Singleton
public class FileNameCompleter
    extends org.jline.reader.impl.completer.FileNameCompleter
    implements Completer
{
  private final Provider<Variables> variables;

  @Inject
  public FileNameCompleter(final Provider<Variables> variables) {
    this.variables = checkNotNull(variables);
  }

  @Override
  protected Path getUserHome() {
    return variables.get().get(SHELL_USER_HOME, Path.class);
  }

  // FIXME: this could probably be simplified to avoid this complex file/path/string handling

  @Override
  protected Path getUserDir() {
    Variables vars = variables.get();
    Object tmp = vars.get(SHELL_USER_DIR);
    assert tmp != null;

    if (tmp instanceof Path) {
      return (Path) tmp;
    }
    else if (tmp instanceof File) {
      return ((File) tmp).toPath();
    }

    return Paths.get(String.valueOf(tmp));
  }
}
