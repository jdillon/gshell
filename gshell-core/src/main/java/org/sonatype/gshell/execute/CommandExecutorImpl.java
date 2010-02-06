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

package org.sonatype.gshell.execute;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.registry.NoSuchCommandException;
import org.sonatype.gshell.command.AliasAction;
import org.sonatype.gshell.command.resolver.Node;
import org.sonatype.gshell.command.support.CommandHelpSupport;
import org.sonatype.gshell.command.support.CommandPreferenceSupport;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.resolver.CommandResolver;
import org.sonatype.gshell.io.StreamJack;
import org.sonatype.gshell.notification.ErrorNotification;
import org.sonatype.gshell.notification.ResultNotification;
import org.sonatype.gshell.parser.CommandLineParser;
import org.sonatype.gshell.parser.CommandLineParser.CommandLine;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.Arguments;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.HelpPrinter;
import org.sonatype.gshell.util.cli2.OpaqueArguments;
import org.sonatype.gshell.util.pref.PreferenceProcessor;
import org.sonatype.gshell.vars.Variables;

import static org.sonatype.gshell.vars.VariableNames.LAST_RESULT;

/**
 * The default {@link org.sonatype.gshell.execute.CommandExecutor} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandExecutorImpl
    implements CommandExecutor
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AliasRegistry aliases;

    private final CommandResolver resolver;

    private final CommandLineParser parser;

    @Inject
    public CommandExecutorImpl(final AliasRegistry aliases, final CommandResolver resolver, final CommandLineParser parser) {
        assert aliases != null;
        this.aliases = aliases;
        assert resolver != null;
        this.resolver = resolver;
        assert parser != null;
        this.parser = parser;
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
                throw (Exception) cause;
            }
            else if (cause instanceof Error) {
                throw (Error) cause;
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

        log.debug("Executing ({}): [{}]", name, Strings.join(args, ", "));

        CommandAction action;
        if (aliases.containsAlias(name)) {
            action = new AliasAction(name, aliases.getAlias(name));
        }
        else {
            Node node = resolver.resolve(name);
            if (node == null) {
                throw new NoSuchCommandException(name);
            }
            action = node.getAction();
        }
        action = action.clone();

        MDC.put(CommandAction.class.getName(), name);

        final Shell lastShell = ShellHolder.set(shell);
        final IO io = shell.getIo();

        StreamJack.maybeInstall(io.streams);

        Object result = null;
        try {
            boolean execute = true;

            PreferenceProcessor pp = CommandPreferenceSupport.createProcessor(action);
            pp.process();

            if (!(action instanceof OpaqueArguments)) {
                CommandHelpSupport help = new CommandHelpSupport();
                CliProcessor clp = help.createProcessor(action);

                // Process the arguments
                clp.process(Arguments.toStringArray(args));

                // Render command-line usage
                if (help.displayHelp) {
                    io.out.println(CommandHelpSupport.getDescription(action));
                    io.out.println();

                    HelpPrinter printer = new HelpPrinter(clp);
                    printer.printUsage(io.out, action.getSimpleName());

                    result = CommandAction.Result.SUCCESS;
                    execute = false;
                }
            }

            if (execute) {
                try {
                    result = action.execute(new CommandContext()
                    {
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
            StreamJack.deregister();
            ShellHolder.set(lastShell);
            MDC.remove(CommandAction.class.getName());
        }

        shell.getVariables().set(LAST_RESULT, result);

        log.debug("Result: {}", result);

        return result;
    }
}