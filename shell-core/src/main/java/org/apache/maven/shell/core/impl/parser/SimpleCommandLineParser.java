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

package org.apache.maven.shell.core.impl.parser;

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.command.Arguments;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.command.CommandLineParser;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link CommandLineParser} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandLineParser.class, hint="simple")
public class SimpleCommandLineParser
    implements CommandLineParser
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public CommandLine parse(final String line) throws Exception {
        assert line != null;

        log.trace("Building command-line for: {}", line);
        
        return new CommandLine()
        {
            public Object execute(final Shell shell, final CommandExecutor executor) throws Exception {
                if (line.trim().startsWith("#")) {
                    return null;
                }
                
                String[] elements = line.split("\\s");

                if (elements.length == 1) {
                    return executor.execute(shell, elements[0], new String[0]);
                }
                else {
                    return executor.execute(shell, elements[0], Arguments.shift(elements));
                }
            }
        };
    }
}