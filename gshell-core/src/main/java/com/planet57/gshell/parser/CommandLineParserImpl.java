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
package com.planet57.gshell.parser;

import java.io.Reader;
import java.io.StringReader;

import com.planet57.gshell.execute.CommandExecutor;
import com.planet57.gshell.parser.impl.ASTCommandLine;
import com.planet57.gshell.parser.impl.Parser;
import com.planet57.gshell.parser.impl.visitor.ExecutingVisitor;
import com.planet57.gshell.parser.impl.visitor.LoggingVisitor;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.ComponentSupport;
import com.planet57.gshell.util.io.Closeables;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CommandLineParser} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class CommandLineParserImpl
  extends ComponentSupport
  implements CommandLineParser
{
  private final Parser parser = new Parser();

  @Override
  public CommandLine parse(final String line) throws Exception {
    checkNotNull(line);

    log.trace("Building command-line for: {}", line);

    Reader reader = new StringReader(line);
    final ASTCommandLine root;
    try {
      root = parser.parse(reader);
    }
    finally {
      Closeables.close(reader);
    }

    // If trace is enabled, the log the parse tree
    if (log.isTraceEnabled()) {
      root.jjtAccept(new LoggingVisitor(log), null);
    }

    return new CommandLine()
    {
      public Object execute(final Shell shell, final CommandExecutor executor) throws Exception {
        ExecutingVisitor visitor = new ExecutingVisitor(shell, executor);
        return root.jjtAccept(visitor, null);
      }
    };
  }
}
