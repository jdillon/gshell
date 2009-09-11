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

package org.apache.maven.shell.core.impl;

import org.apache.maven.shell.Arguments;
import org.apache.maven.shell.Command;
import org.apache.maven.shell.CommandContext;
import org.apache.maven.shell.CommandException;
import org.apache.maven.shell.CommandExecutor;
import org.apache.maven.shell.CommandSupport;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default {@link CommandLineExecutor} component.
 *
 * @version $Rev$ $Date$
 */
@Component(role=CommandExecutor.class)
public class CommandExecutorImpl
    implements CommandExecutor
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement
    private CommandRegistry commandRegistry;
    
    public Object execute(final ShellContext context, final String line) throws Exception {
        assert context != null;
        assert line != null;

        log.debug("Parsing command from line: {}", line);

        String[] elements = line.split(" ");

        if (elements.length == 1) {
            return execute(context, elements[0], new String[0]);
        }
        else {
            return execute(context, elements[0], Arguments.shift(elements));
        }
    }

    public Object execute(final ShellContext context, final String... args) throws Exception {
        assert context != null;
        assert args != null;

        return execute(context, String.valueOf(args[0]), Arguments.shift(args));
    }
    
    public Object execute(final ShellContext context, final String name, final String[] args) throws Exception {
        assert context != null;
        assert name != null;
        assert args != null;

        log.debug("Executing ({}): [{}]", name, Arguments.asString(args));

        Command command = resolveCommand(name);

        final IO io = context.getIo();

        Object result;
        try {
            result = command.execute(new CommandContext() {
                public Shell getShell() {
                    return context.getShell();
                }
                
                public String[] getArguments() {
                    return args;
                }

                public IO getIo() {
                    return io;
                }

                public Variables getVariables() {
                    return context.getVariables();
                }
            });
        }
        finally {
            io.flush();
        }

        return result;
    }

    private Command resolveCommand(final String name) throws CommandException {
        assert name != null;

        log.debug("Resolving command for name: {}", name);

        Command command;

        command = resolveAliasCommand(name);

        if (command == null) {
            command = resolveRegisteredCommand(name);
        }
        
        if (command == null) {
            throw new CommandException("Unable to resolve command: " + name);
        }

        log.debug("Resolved command: {}", command);

        return command;
    }

    private Command resolveAliasCommand(final String name) throws CommandException {
        assert name != null;
        assert aliasRegistry != null;

        if (aliasRegistry.containsAlias(name)) {
            final String alias = aliasRegistry.getAlias(name);

            return new CommandSupport() {
                public String getName() {
                    return name;
                }

                public Object execute(final CommandContext context) throws Exception {
                    assert context != null;

                    log.debug("Executing alias ({}) -> {}", name, alias);

                    return context.getShell().execute(alias);
                }
            };
        }

        return null;
    }

    private Command resolveRegisteredCommand(final String name) throws CommandException {
        assert name != null;
        assert commandRegistry != null;

        if (commandRegistry.containsCommand(name)) {
            return commandRegistry.getCommand(name);
        }

        return null;
    }
}