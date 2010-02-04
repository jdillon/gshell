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

package org.sonatype.gshell.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import jline.Terminal;
import jline.console.Completer;
import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.alias.AliasNameCompleter;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.alias.AliasRegistryImpl;
import org.sonatype.gshell.command.CommandNameCompleter;
import org.sonatype.gshell.command.CommandsCompleter;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRegistryImpl;
import org.sonatype.gshell.command.resolver.CommandResolver;
import org.sonatype.gshell.command.resolver.CommandResolverImpl;
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
import org.sonatype.gshell.io.PromptReader;
import org.sonatype.gshell.parser.CommandLineParser;
import org.sonatype.gshell.parser.CommandLineParserImpl;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.vars.VariableNameCompleter;
import org.sonatype.gshell.vars.Variables;

import java.io.IOException;

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
        bind(HelpContentLoader.class).to(HelpContentLoaderImpl.class);
        bind(HelpPageManager.class).to(HelpPageManagerImpl.class);
        bind(CommandLineParser.class).to(CommandLineParserImpl.class);
        bind(CommandExecutor.class).to(CommandExecutorImpl.class);
        bind(CommandResolver.class).to(CommandResolverImpl.class);
        bind(CommandRegistrar.class).to(GuiceCommandRegistrar.class);

        bind(Completer.class).annotatedWith(Names.named("commands")).to(CommandsCompleter.class);
        bind(Completer.class).annotatedWith(Names.named("command-name")).to(CommandNameCompleter.class);
        bind(Completer.class).annotatedWith(Names.named("alias-name")).to(AliasNameCompleter.class);
        bind(Completer.class).annotatedWith(Names.named("file-name")).to(FileNameCompleter.class);
        bind(Completer.class).annotatedWith(Names.named("variable-name")).to(VariableNameCompleter.class);
        bind(Completer.class).annotatedWith(Names.named("meta-help-page-name")).to(MetaHelpPageNameCompleter.class);
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