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
package com.planet57.gshell.commands.shell;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.cli2.Option;
import org.jline.builtins.Less;
import org.jline.builtins.Source;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Less action.
 *
 * @since 3.0
 */
@Command(name = "less", description = "Source pager")
public class LessAction
    extends CommandActionSupport
{
  // TODO: expose more options; see Commands.less() in jline-builtins

  @Nullable
  @Option(name = "n", longName = "line-numbers", description = "Display line numbers for each line")
  private Boolean lineNumbers;

  // TODO: consider exposing a file/url source adapter and converter

  @Nullable
  @Argument(description = "File to display", token = "FILE")
  private File file;

  @Inject
  public void installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    Less less = new Less(io.terminal);

    if (lineNumbers != null) {
      less.printLineNumbers = lineNumbers;
    }

    Source input;
    if (file == null) {
      input = new Source.InputStreamSource(io.streams.in, false, null);
    }
    else {
      input = new Source.PathSource(file.toPath(), file.getName());
    }
    less.run(input);

    return null;
  }
}
