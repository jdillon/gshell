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

package org.sonatype.gshell.builder.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import jline.console.Completer;
import jline.console.completers.AggregateCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.console.completer.AliasNameCompleter;
import org.sonatype.gshell.console.completer.CommandsCompleter;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellImpl;
import org.sonatype.gshell.vars.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds {@link org.sonatype.gshell.shell.Shell} instances using Guice to wire components.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class GuiceShellBuilder
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Injector injector;

    private Branding branding;

    private IO io;

    private Variables variables;

    private boolean registerCommands = true;

    private ConsolePrompt prompt;

    private ConsoleErrorHandler errorHandler;

    private final List<Completer> completers = new ArrayList<Completer>();

    protected Injector createInjector() {
        return Guice.createInjector(Stage.PRODUCTION, new CoreModule());
    }

    public Injector getInjector() {
        if (injector == null) {
            injector = createInjector();
        }
        return injector;
    }

    public GuiceShellBuilder setInjector(final Injector injector) {
        assert injector != null;
        this.injector = injector;
        return this;
    }

    public GuiceShellBuilder setBranding(final Branding branding) {
        this.branding = branding;
        return this;
    }

    public GuiceShellBuilder setIo(final IO io) {
        this.io = io;
        return this;
    }

    public GuiceShellBuilder setVariables(final Variables variables) {
        this.variables = variables;
        return this;
    }

    public GuiceShellBuilder setRegisterCommands(final boolean flag) {
        this.registerCommands = flag;
        return this;
    }

    public GuiceShellBuilder setPrompt(final ConsolePrompt prompt) {
        this.prompt = prompt;
        return this;
    }

    public GuiceShellBuilder setErrorHandler(final ConsoleErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public GuiceShellBuilder addCompleter(final Completer completer) {
        assert completer != null;
        completers.add(completer);
        return this;
    }

    public Shell create() throws Exception {
        if (branding == null) {
            throw new IllegalStateException("Missing branding");
        }

        Injector injector = getInjector();

        // Create the shell instance
        CommandExecutor executor = injector.getInstance(CommandExecutor.class);
        EventManager eventManager = injector.getInstance(EventManager.class);
        
        ShellImpl shell = new ShellImpl(eventManager, executor, branding, io, variables);
        shell.setPrompt(prompt);
        shell.setErrorHandler(errorHandler);

        // Maybe register default commands
        if (registerCommands) {
            injector.getInstance(CommandRegistrar.class).registerCommands();
        }

        addCompleter(new AggregateCompleter(injector.getInstance(AliasNameCompleter.class), injector.getInstance(CommandsCompleter.class)));
        
        shell.setCompleters(completers);

        log.debug("Created shell: {}", shell);

        return shell;
    }
}