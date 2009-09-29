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

package org.apache.gshell.core;

import jline.Completor;
import org.apache.gshell.console.Console;
import org.apache.gshell.core.ShellImpl;
import org.apache.gshell.execute.CommandExecutor;
import org.apache.gshell.io.IO;
import org.apache.gshell.registry.CommandRegistrar;
import org.apache.gshell.Branding;
import org.apache.gshell.Variables;
import org.apache.gshell.Shell;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds {@link org.apache.gshell.Shell} instances using Plexus.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class PlexusShellBuilder
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private PlexusContainer container;

    private Branding branding;

    private IO io;

    private Variables variables;

    private boolean registerCommands = true;

    private Console.Prompter prompter;

    private Console.ErrorHandler errorHandler;

    private final List<Completor> completers = new ArrayList<Completor>();

    public PlexusShellBuilder setContainer(final PlexusContainer container) {
        this.container = container;
        return this;
    }

    public PlexusContainer getContainer() throws PlexusContainerException {
        if (container == null) {
            container = new DefaultPlexusContainer(new DefaultContainerConfiguration());
        }
        return container;
    }

    public PlexusShellBuilder setBranding(final Branding branding) {
        this.branding = branding;
        return this;
    }

    public PlexusShellBuilder setIo(final IO io) {
        this.io = io;
        return this;
    }

    public PlexusShellBuilder setVariables(final Variables variables) {
        this.variables = variables;
        return this;
    }

    public PlexusShellBuilder setRegisterCommands(final boolean flag) {
        this.registerCommands = flag;
        return this;
    }

    public PlexusShellBuilder setPrompter(final Console.Prompter prompter) {
        this.prompter = prompter;
        return this;
    }

    public PlexusShellBuilder setErrorHandler(final Console.ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public PlexusShellBuilder addCompleter(final Completor completer) {
        assert completer != null;
        completers.add(completer);
        return this;
    }

    public Shell create() throws Exception {
        if (branding == null) {
            throw new IllegalStateException("Missing branding");
        }

        // Create the shell instance
        CommandExecutor executor = container.lookup(CommandExecutor.class);
        ShellImpl shell = new ShellImpl(branding, executor, io, variables);
        shell.setPrompter(prompter);
        shell.setErrorHandler(errorHandler);
        shell.setCompleters(completers);

        // Maybe register default commands
        if (registerCommands) {
            getContainer().lookup(CommandRegistrar.class).registerCommands();
        }

        log.debug("Created shell: {}", shell);

        return shell;
    }
}