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

import com.planet57.gossip.Level;
import com.planet57.gshell.parser.impl.ASTCommandLine;
import com.planet57.gshell.parser.impl.Parser;
import com.planet57.gshell.parser.impl.eval.Evaluator;
import com.planet57.gshell.parser.impl.visitor.ExecutingVisitor;
import com.planet57.gshell.parser.impl.visitor.LoggingVisitor;
import org.sonatype.goodies.common.ComponentSupport;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CommandLineParser}.
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
  private final Evaluator evaluator;

  private final Parser parser = new Parser();

  @Inject
  public CommandLineParserImpl(final Evaluator evaluator) {
    this.evaluator = checkNotNull(evaluator);
  }

  @Override
  public CommandLine parse(final String line) throws Exception {
    checkNotNull(line);

    log.trace("Building command-line for: {}", line);

    final ASTCommandLine root;
    try (Reader reader = new StringReader(line)) {
      root = parser.parse(reader);
    }

    // If trace is enabled, the log the parse tree
    if (log.isTraceEnabled()) {
      root.jjtAccept(new LoggingVisitor(log, Level.TRACE), null);
    }

    return (shell, executor) -> {
      ExecutingVisitor visitor = new ExecutingVisitor(shell, executor, evaluator);
      return root.jjtAccept(visitor, null);
    };
  }
}
