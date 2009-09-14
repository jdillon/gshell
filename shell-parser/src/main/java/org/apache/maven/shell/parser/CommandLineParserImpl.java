/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.parser;

import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.command.CommandLineParser;
import org.apache.maven.shell.io.Closer;
import org.apache.maven.shell.parser.impl.visitor.ExecutingVisitor;
import org.apache.maven.shell.parser.impl.visitor.LoggingVisitor;
import org.apache.maven.shell.parser.impl.Parser;
import org.apache.maven.shell.parser.impl.ASTCommandLine;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.StringReader;

/**
 * The default {@link CommandLineParser} component.
 *
 * @version $Rev$ $Date$
 */
@Component(role=CommandLineParser.class)
public class CommandLineParserImpl
    implements CommandLineParser
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Parser parser = new Parser();

    public CommandLine parse(final String line) throws Exception {
        assert line != null;

        log.debug("Building command-line for: {}", line);

        Reader reader = new StringReader(line);
        final ASTCommandLine root;
        try {
            root = parser.parse(reader);
        }
        finally {
            Closer.close(reader);
        }

        // If debug is enabled, the log the parse tree
        if (log.isDebugEnabled()) {
            LoggingVisitor logger = new LoggingVisitor(log);
            root.jjtAccept(logger, null);
        }

        return new CommandLine()
        {
            public Object execute(final ShellContext context, final CommandExecutor executor) throws Exception {
                assert context != null;

                ExecutingVisitor visitor = new ExecutingVisitor(context, executor);
                return root.jjtAccept(visitor, null);
            }
        };
    }
}