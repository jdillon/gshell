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

package org.sonatype.gshell.core.simple;

import jline.console.completers.AggregateCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.Branding;
import org.sonatype.gshell.Shell;
import org.sonatype.gshell.ShellFactory;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.CommandDocumenter;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.Console;
import org.sonatype.gshell.core.ShellImpl;
import org.sonatype.gshell.core.command.CommandDocumenterImpl;
import org.sonatype.gshell.core.commands.AliasCommand;
import org.sonatype.gshell.core.commands.EchoCommand;
import org.sonatype.gshell.core.commands.ExitCommand;
import org.sonatype.gshell.core.commands.HelpCommand;
import org.sonatype.gshell.core.commands.HistoryCommand;
import org.sonatype.gshell.core.commands.InfoCommand;
import org.sonatype.gshell.core.commands.RecallHistoryCommand;
import org.sonatype.gshell.core.commands.SetCommand;
import org.sonatype.gshell.core.commands.SourceCommand;
import org.sonatype.gshell.core.commands.UnaliasCommand;
import org.sonatype.gshell.core.commands.UnsetCommand;
import org.sonatype.gshell.core.completer.AliasNameCompleter;
import org.sonatype.gshell.core.completer.CommandNameCompleter;
import org.sonatype.gshell.core.completer.CommandsCompleter;
import org.sonatype.gshell.core.completer.FileNameCompleter;
import org.sonatype.gshell.core.completer.VariableNameCompleter;
import org.sonatype.gshell.core.event.EventManagerImpl;
import org.sonatype.gshell.core.execute.CommandExecutorImpl;
import org.sonatype.gshell.core.parser.CommandLineParserImpl;
import org.sonatype.gshell.core.registry.AliasRegistryImpl;
import org.sonatype.gshell.core.registry.CommandRegistryImpl;
import org.sonatype.gshell.core.registry.CommandResolverImpl;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.execute.CommandLineParser;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.registry.CommandResolver;

//
// TODO: See how we can generify this builder so we can effectivly share its base-code with other builders
//

/**
 * Builds {@link Shell} instances w/o any IoC container.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class SimpleShellBuilder
    implements ShellFactory
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected Branding branding;

    protected IO io;

    protected Variables variables;

    protected boolean registerCommands = true;

    protected Console.Prompter prompter;

    protected Console.ErrorHandler errorHandler;

    public SimpleShellBuilder setBranding(final Branding branding) {
        this.branding = branding;
        return this;
    }

    public SimpleShellBuilder setIo(final IO io) {
        this.io = io;
        return this;
    }

    public SimpleShellBuilder setVariables(final Variables variables) {
        this.variables = variables;
        return this;
    }

    public SimpleShellBuilder setRegisterCommands(final boolean flag) {
        this.registerCommands = flag;
        return this;
    }

    public SimpleShellBuilder setPrompter(final Console.Prompter prompter) {
        this.prompter = prompter;
        return this;
    }

    public SimpleShellBuilder setErrorHandler(final Console.ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public Shell create() throws Exception {
        if (branding == null) {
            throw new IllegalStateException("Missing branding");
        }

        ComponentsImpl components = new ComponentsImpl();

        // Maybe register default commands
        if (registerCommands) {
            registerCommands(components);
        }

        Shell shell = createShell(components);
        log.debug("Created shell: {}", shell);

        return shell;
    }

    protected void registerCommands(final Components components) throws Exception {
        assert components != null;

        CommandRegistry registry = components.getCommandRegistry();

        registry.registerCommand("help", new HelpCommand(components.getAliasRegistry(), registry, components.getCommandDocumenter())
                .installCompleters(components.getAliasNameCompleter(), components.getCommandNameCompleter()));
        registry.registerCommand("info", new InfoCommand());
        registry.registerCommand("exit", new ExitCommand());
        registry.registerCommand("set", new SetCommand()
                .installCompleters(components.getVariableNameCompleter()));
        registry.registerCommand("unset", new UnsetCommand()
                .installCompleters(components.getVariableNameCompleter()));
        registry.registerCommand("history", new HistoryCommand());
        registry.registerCommand("recall", new RecallHistoryCommand());
        registry.registerCommand("source", new SourceCommand()
                .installCompleters(components.getFileNameCompleter()));
        registry.registerCommand("alias", new AliasCommand(components.getAliasRegistry()));
        registry.registerCommand("unalias", new UnaliasCommand(components.getAliasRegistry())
                .installCompleters(components.getAliasNameCompleter()));
        registry.registerCommand("echo", new EchoCommand());
    }

    protected Shell createShell(final Components components) throws Exception {
        assert components != null;

        // Create the shell instance
        ShellImpl shell = new ShellImpl(components.getEventManager(), components.getCommandExecutor(), branding, io, variables);
        shell.setPrompter(prompter);
        shell.setErrorHandler(errorHandler);
        shell.setCompleters(new AggregateCompleter(components.getAliasNameCompleter(), components.getCommandsCompleter()));

        return shell;
    }

    public static interface Components
    {
        AliasRegistry getAliasRegistry();

        CommandDocumenter getCommandDocumenter();

        CommandExecutor getCommandExecutor();

        CommandLineParser getCommandLineParser();

        CommandRegistrar getCommandRegistrar();

        CommandRegistry getCommandRegistry();

        CommandResolver getCommandResolver();

        EventManager getEventManager();

        FileNameCompleter getFileNameCompleter();

        VariableNameCompleter getVariableNameCompleter();

        AliasNameCompleter getAliasNameCompleter();

        CommandNameCompleter getCommandNameCompleter();

        CommandsCompleter getCommandsCompleter();
    }

    private class ComponentsImpl
        implements Components
    {
        private final EventManager eventManager;

        private final CommandRegistry commandRegistry;

        private final AliasRegistry aliasRegistry;

        private final CommandResolver commandResolver;

        private final CommandLineParser commandLineParser;

        private final CommandDocumenter commandDocumenter;

        private final CommandExecutor commandExecutor;

        private final CommandRegistrar commandRegistrar;

        private final AliasNameCompleter aliasNameCompleter;

        private final CommandNameCompleter commandNameCompleter;

        private final CommandsCompleter commandsCompleter;

        private final VariableNameCompleter variableNameCompleter;

        private final FileNameCompleter fileNameCompleter;

        public ComponentsImpl() throws Exception {
            // Core components
            eventManager = new EventManagerImpl();
            commandRegistry = new CommandRegistryImpl(eventManager);
            aliasRegistry = new AliasRegistryImpl(eventManager);
            commandResolver = new CommandResolverImpl(aliasRegistry, commandRegistry);
            commandLineParser = new CommandLineParserImpl();
            commandDocumenter = new CommandDocumenterImpl();
            commandExecutor = new CommandExecutorImpl(commandResolver, commandLineParser, commandDocumenter);
            commandRegistrar = new SimpleCommandRegistrar(commandRegistry);

            // Core completers
            aliasNameCompleter = new AliasNameCompleter(eventManager, aliasRegistry);
            commandNameCompleter = new CommandNameCompleter(eventManager, commandRegistry);
            commandsCompleter = new CommandsCompleter(eventManager, commandRegistry);
            variableNameCompleter = new VariableNameCompleter();
            fileNameCompleter = new FileNameCompleter();
        }

        public EventManager getEventManager() {
            return eventManager;
        }

        public CommandRegistry getCommandRegistry() {
            return commandRegistry;
        }

        public AliasRegistry getAliasRegistry() {
            return aliasRegistry;
        }

        public CommandResolver getCommandResolver() {
            return commandResolver;
        }

        public CommandLineParser getCommandLineParser() {
            return commandLineParser;
        }

        public CommandDocumenter getCommandDocumenter() {
            return commandDocumenter;
        }

        public CommandExecutor getCommandExecutor() {
            return commandExecutor;
        }

        public CommandRegistrar getCommandRegistrar() {
            return commandRegistrar;
        }

        public AliasNameCompleter getAliasNameCompleter() {
            return aliasNameCompleter;
        }


        public CommandNameCompleter getCommandNameCompleter() {
            return commandNameCompleter;
        }

        public CommandsCompleter getCommandsCompleter() {
            return commandsCompleter;
        }

        public VariableNameCompleter getVariableNameCompleter() {
            return variableNameCompleter;
        }

        public FileNameCompleter getFileNameCompleter() {
            return fileNameCompleter;
        }
    }
}