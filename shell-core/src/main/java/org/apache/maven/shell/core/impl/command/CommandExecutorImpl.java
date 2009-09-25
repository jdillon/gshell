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

import org.apache.maven.shell.Arguments;
import org.apache.maven.shell.execute.CommandExecutor;
import org.apache.maven.shell.execute.CommandLineParser;
import org.apache.maven.shell.execute.CommandLineParser.CommandLine;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.cli.Processor;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandDocumenter;
import org.apache.maven.shell.command.OpaqueArguments;
import org.apache.maven.shell.i18n.PrefixingMessageSource;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.SystemInputOutputHijacker;
import org.apache.maven.shell.notification.ErrorNotification;
import org.apache.maven.shell.notification.ResultNotification;
import org.apache.maven.shell.registry.CommandResolver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The default {@link CommandExecutor} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
@Component(role=CommandExecutor.class)
public class CommandExecutorImpl
    implements CommandExecutor
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private CommandResolver resolver;

    @Requirement
    private CommandLineParser parser;

    @Requirement
    private CommandDocumenter documeter;

    public CommandExecutorImpl() {}

    public CommandExecutorImpl(final CommandResolver resolver, final CommandLineParser parser, final CommandDocumenter documenter) {
        assert resolver != null;
        this.resolver = resolver;

        assert parser != null;
        this.parser = parser;

        assert documenter != null;
        this.documeter = documenter;
    }

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

        Command command = resolver.resolveCommand(name);

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
                clp.setMessages(new PrefixingMessageSource(command.getMessages(), CommandDocumenter.COMMAND_DOT));
                CommandHelpSupport help = new CommandHelpSupport();
                clp.addBean(help);

                // Process the arguments
                clp.process(Arguments.toStringArray(args));

                // Render command-line usage
                if (help.displayHelp) {
                    log.trace("Render command-line usage");
                    documeter.renderUsage(command, io);
                    result = Command.Result.SUCCESS;
                    execute = false;
                }
            }

            if (execute) {
                try {
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
                catch (ResultNotification n) {
                    result = n.getResult();   
                }
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
}