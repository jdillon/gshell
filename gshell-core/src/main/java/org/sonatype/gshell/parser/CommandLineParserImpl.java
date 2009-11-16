/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.Shell;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.execute.CommandLineParser;
import org.sonatype.gshell.parser.impl.ASTCommandLine;
import org.sonatype.gshell.parser.impl.Parser;
import org.sonatype.gshell.parser.impl.visitor.ExecutingVisitor;
import org.sonatype.gshell.parser.impl.visitor.LoggingVisitor;
import org.sonatype.gshell.util.io.Closer;

import java.io.Reader;
import java.io.StringReader;

/**
 * The default {@link org.sonatype.gshell.execute.CommandLineParser} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandLineParserImpl
    implements CommandLineParser
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Parser parser = new Parser();

    public CommandLine parse(final String line) throws Exception {
        assert line != null;

        log.trace("Building command-line for: {}", line);

        Reader reader = new StringReader(line);
        final ASTCommandLine root;
        try {
            root = parser.parse(reader);
        }
        finally {
            Closer.close(reader);
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