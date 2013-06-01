/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import jline.Terminal;
import jline.console.completer.Completer;
import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.alias.AliasNameCompleter;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.alias.AliasRegistryImpl;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.registry.CommandNameCompleter;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRegistryImpl;
import org.sonatype.gshell.command.registry.CommandsCompleter;
import org.sonatype.gshell.command.resolver.CommandResolver;
import org.sonatype.gshell.command.resolver.CommandResolverImpl;
import org.sonatype.gshell.command.resolver.NodePathCompleter;
import org.sonatype.gshell.console.completer.FileNameCompleter;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.event.EventManagerImpl;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.execute.CommandExecutorImpl;
import org.sonatype.gshell.help.HelpContentLoader;
import org.sonatype.gshell.help.HelpContentLoaderImpl;
import org.sonatype.gshell.help.HelpPageManager;
import org.sonatype.gshell.help.HelpPageManagerImpl;
import org.sonatype.gshell.help.MetaHelpPageNameCompleter;
import org.sonatype.gshell.util.io.PromptReader;
import org.sonatype.gshell.logging.LevelNameCompleter;
import org.sonatype.gshell.logging.LoggerNameCompleter;
import org.sonatype.gshell.parser.CommandLineParser;
import org.sonatype.gshell.parser.CommandLineParserImpl;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.variables.VariableNameCompleter;
import org.sonatype.gshell.variables.Variables;

import java.io.IOException;

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