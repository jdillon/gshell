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
import java.nio.file.Paths;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

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
@Command(name = "less")
public class LessAction
    extends CommandActionSupport
{
  // TODO: expose more options; see Commands.less() in jline-builtins

  @Option(name = "n", longName = "line-numbers")
  private Boolean lineNumbers;

  // TODO: leaving this as "source" for now, as this could be adapted to url or file/path
  @Argument(required = true)
  private File source;

  @Inject
  public void installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Less less = new Less(context.getIo().terminal);

    if (lineNumbers != null) {
      less.printLineNumbers = lineNumbers;
    }

    Source input = new Source.PathSource(source.toPath(), source.getName());
    less.run(input);

    return Result.SUCCESS;
  }
}
