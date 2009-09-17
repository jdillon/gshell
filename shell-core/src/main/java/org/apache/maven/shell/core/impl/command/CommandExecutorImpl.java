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

package org.apache.maven.shell.core.impl.command;

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.cli.Processor;
import org.apache.maven.shell.command.Arguments;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandDocumenter;
import org.apache.maven.shell.command.CommandException;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.command.CommandLineParser;
import org.apache.maven.shell.command.CommandLineParser.CommandLine;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.command.OpaqueArguments;
import org.apache.maven.shell.i18n.PrefixingMessageSource;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.SystemInputOutputHijacker;
import org.apache.maven.shell.notification.ErrorNotification;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The default {@link CommandExecutor} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
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

    @Requirement
    private CommandLineParser parser;

    @Requirement
    private CommandDocumenter commandDocumeter;
    private static final String COMMAND_DOT = "command.";

    public Object execute(final Shell shell, final String line) throws Exception {
        assert shell != null;
        assert line != null;

        if (line.trim().length() == 0) {
            log.trace("Ignoring empty line");
            return null;
        }

        final Shell lastShell = ShellHolder.set(shell);
        
        CommandLine cl = parser.parse(line);
        
        try {
            return cl.execute(shell, this);
        }
        catch (ErrorNotification n) {
            // Decode the error notification
            Throwable cause = n.getCause();

            if (cause instanceof Exception) {
                throw (Exception)cause;
            }
            else if (cause instanceof Error) {
                throw (Error)cause;
            }
            else {
                throw n;
            }
        }
        finally {
            ShellHolder.set(lastShell);
        }
    }

    public Object execute(final Shell shell, final Object... args) throws Exception {
        assert shell != null;
        assert args != null;

        return execute(shell, String.valueOf(args[0]), Arguments.shift(args));
    }
    
    public Object execute(final Shell shell, final String name, final Object[] args) throws Exception {
        assert shell != null;
        assert name != null;
        assert args != null;

        log.debug("Executing ({}): [{}]", name, StringUtils.join(args, ", "));

        Command command = resolveCommand(name);

        MDC.put(Command.class.getName(), name);

        final Shell lastShell = ShellHolder.set(shell);

        final IO io = shell.getIo();

        // Hijack the system output streams
        if (!SystemInputOutputHijacker.isInstalled()) {
            SystemInputOutputHijacker.install(io.streams);
        }
        else {
            SystemInputOutputHijacker.register(io.streams);
        }
        
        Object result = null;
        try {
            boolean execute = true;

            if (!(command instanceof OpaqueArguments)) {
                Processor clp = new Processor(command);
                clp.setMessages(new PrefixingMessageSource(command.getMessages(), COMMAND_DOT));
                CommandHelpSupport help = new CommandHelpSupport();
                clp.addBean(help);

                // Process the arguments
                clp.process(Arguments.toStringArray(args));

                // Render command-line usage
                if (help.displayHelp) {
                    log.trace("Render command-line usage");
                    commandDocumeter.renderUsage(command, io);
                    result = Command.Result.SUCCESS;
                    execute = false;
                }
            }

            if (execute) {
                result = command.execute(new CommandContext() {
                    public Shell getShell() {
                        return shell;
                    }

                    public Object[] getArguments() {
                        return args;
                    }

                    public IO getIo() {
                        return io;
                    }

                    public Variables getVariables() {
                        return shell.getVariables();
                    }
                });
            }
        }
        finally {
            io.flush();

            SystemInputOutputHijacker.deregister();

            ShellHolder.set(lastShell);
            
            MDC.remove(Command.class.getName());
        }

        log.debug("Result: {}", result);

        return result;
    }

    //
    // Command resolution
    //

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
            return new Alias(name, aliasRegistry.getAlias(name));
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

    private static class Alias
        extends CommandSupport
        implements OpaqueArguments
    {
        private final String name;

        private final String target;

        public Alias(final String name, final String target) {
            assert name != null;
            assert target != null;

            this.name = name;
            this.target = target;
        }

        public String getName() {
            return name;
        }

        public Object execute(final CommandContext context) throws Exception {
            assert context != null;

            String alias = target;

            // Need to append any more arguments in the context
            Object[] args = context.getArguments();
            if (args.length > 0) {
                alias = String.format("%s %s", target, StringUtils.join(args, " "));
            }

            log.debug("Executing alias ({}) -> {}", name, alias);

            return context.getShell().execute(alias);
        }
    }
}