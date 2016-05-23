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
package com.planet57.gshell.guice;

import java.io.IOException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.planet57.gshell.alias.AliasNameCompleter;
import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.AliasRegistryImpl;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandNameCompleter;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.command.registry.CommandRegistry;
import com.planet57.gshell.command.registry.CommandRegistryImpl;
import com.planet57.gshell.command.registry.CommandsCompleter;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.CommandResolverImpl;
import com.planet57.gshell.command.resolver.NodePathCompleter;
import com.planet57.gshell.console.completer.FileNameCompleter;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.event.EventManagerImpl;
import com.planet57.gshell.execute.CommandExecutor;
import com.planet57.gshell.execute.CommandExecutorImpl;
import com.planet57.gshell.help.HelpContentLoader;
import com.planet57.gshell.help.HelpContentLoaderImpl;
import com.planet57.gshell.help.HelpPageManager;
import com.planet57.gshell.help.HelpPageManagerImpl;
import com.planet57.gshell.help.MetaHelpPageNameCompleter;
import com.planet57.gshell.logging.LevelNameCompleter;
import com.planet57.gshell.logging.LoggerNameCompleter;
import com.planet57.gshell.parser.CommandLineParser;
import com.planet57.gshell.parser.CommandLineParserImpl;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.io.PromptReader;
import com.planet57.gshell.variables.VariableNameCompleter;
import com.planet57.gshell.variables.Variables;
import jline.Terminal;
import jline.console.completer.Completer;
import org.fusesource.jansi.AnsiRenderer;

import static com.google.inject.name.Names.named;

/**
 * GShell core module.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CoreModule
    extends AbstractModule
{
    @Override
    protected void configure() {
        bind(EventManager.class).to(EventManagerImpl.class);
        bind(AliasRegistry.class).to(AliasRegistryImpl.class);
        bind(CommandRegistry.class).to(CommandRegistryImpl.class);
        bind(CommandRegistrar.class).to(CommandRegistrarImpl.class);
        bind(HelpContentLoader.class).to(HelpContentLoaderImpl.class);
        bind(HelpPageManager.class).to(HelpPageManagerImpl.class);
        bind(CommandLineParser.class).to(CommandLineParserImpl.class);
        bind(CommandExecutor.class).to(CommandExecutorImpl.class);
        bind(CommandResolver.class).to(CommandResolverImpl.class);

        bind(Completer.class).annotatedWith(named("commands")).to(CommandsCompleter.class);
        bind(Completer.class).annotatedWith(named("command-name")).to(CommandNameCompleter.class);
        bind(Completer.class).annotatedWith(named("node-path")).to(NodePathCompleter.class);
        bind(Completer.class).annotatedWith(named("alias-name")).to(AliasNameCompleter.class);
        bind(Completer.class).annotatedWith(named("file-name")).to(FileNameCompleter.class);
        bind(Completer.class).annotatedWith(named("variable-name")).to(VariableNameCompleter.class);
        bind(Completer.class).annotatedWith(named("meta-help-page-name")).to(MetaHelpPageNameCompleter.class);
        bind(Completer.class).annotatedWith(named("level-name")).to(LevelNameCompleter.class);
        bind(Completer.class).annotatedWith(named("logger-name")).to(LoggerNameCompleter.class);
    }

    @Provides
    private Shell provideShell() {
        return ShellHolder.get();
    }

    @Provides
    private IO provideIo() {
        return provideShell().getIo();
    }

    @Provides
    private Variables provideVariables() {
        return provideShell().getVariables();
    }

    @Provides
    private Terminal provideTerminal() {
        return provideIo().getTerminal();
    }

    @Provides
    private PromptReader providePromptReader() throws IOException {
        IO io = provideIo();

        return new PromptReader(io.streams, io.getTerminal())
        {
            @Override
            public String readLine(String prompt, Validator validator) throws IOException {
                return super.readLine(AnsiRenderer.render(prompt), validator);
            }

            @Override
            public String readLine(String prompt, char mask, Validator validator) throws IOException {
                return super.readLine(AnsiRenderer.render(prompt), mask, validator);
            }

            @Override
            public String readPassword(String prompt, Validator validator) throws IOException {
                return super.readPassword(AnsiRenderer.render(prompt), validator);
            }
        };
    }
}