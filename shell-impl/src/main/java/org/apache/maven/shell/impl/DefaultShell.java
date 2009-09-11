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

package org.apache.maven.shell.impl;

import jline.Completor;
import jline.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.CommandExecutor;
import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.Closer;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.console.Console;
import org.apache.maven.shell.console.AggregateCompleter;
import org.apache.maven.shell.console.JLineConsole;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The default {@link Shell} component.
 *
 * @version $Rev$ $Date$
 */
public class DefaultShell
    implements Shell, Initializable
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    // @Requirement
    private CommandExecutor executor;

    // @Requirement
    private History history;

    // @Requirement
    private List<Completor> completers;

    // @Requirement
    private Console.Prompter prompter;

    // @Requirement
    private Console.ErrorHandler errorHandler;

    private ShellContext context;

    private boolean opened;

    private synchronized void ensureOpened() {
        if (!opened) {
            throw new IllegalStateException("Shell has not been opened or has been closed");
        }
    }

    public synchronized boolean isOpened() {
        return true;
    }

    // @PostConstruct
    public synchronized void init() throws Exception {
        if (opened) {
            throw new IllegalStateException("Shell is already opened");
        }

        log.debug("Initializing");

        // Each shell gets its own variables, using application variables for defaults
        final Variables vars = new Variables();

        // HACK: Should get this from somewhere, but for now fuck it
        final IO io = new IO();

        context = new ShellContext()
        {
            public Shell getShell() {
                return DefaultShell.this;
            }

            public IO getIo() {
                return io;
            }

            public Variables getVariables() {
                return vars;
            }
        };

        // vars.set("gshell.prompt", application.getModel().getBranding().getPrompt());
        // vars.set(CommandResolver.GROUP, "/");
        // vars.set("gshell.username", application.getUserName());
        // vars.set("gshell.hostname", application.getLocalHost());

        // HACK: Add history for the 'history' command, since its not part of the Shell intf it can't really access it
        vars.set("shell.internal.history", getHistory(), true);

        loadProfileScripts();

        opened = true;
    }

    public void initialize() throws InitializationException {
        try {
            init();
        }
        catch (Exception e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    public synchronized void close() {
        log.debug("Closing");

        opened = false;
    }

    public ShellContext getContext() {
        ensureOpened();

        if (context == null) {
            throw new IllegalStateException("Shell context has not been initialized");
        }
        return context;
    }


    public void setCompleters(final List<Completor> completers) {
        assert completers != null;

        this.completers = completers;
    }

    public History getHistory() {
        if (history == null) {
            throw new IllegalStateException("Missing configuration property: history");
        }
        return history;
    }

    public void setHistory(final History history) {
        this.history = history;
    }

    public boolean isInteractive() {
        return true;
    }

    public Object execute(final String line) throws Exception {
        ensureOpened();

        assert executor != null;
        return executor.execute(getContext(), line);
    }

    public Object execute(final String command, final String[] args) throws Exception {
        ensureOpened();

        assert executor != null;
        return executor.execute(getContext(), command, args);
    }

    public Object execute(final String... args) throws Exception {
        ensureOpened();

        assert executor != null;
        return executor.execute(getContext(), args);
    }

    public void run(final String... args) throws Exception {
        assert args != null;

        ensureOpened();

        log.debug("Starting interactive console; args: {}", args);

        // loadUserScript(application.getModel().getBranding().getInteractiveScriptName());

        // Setup 2 final refs to allow our executor to pass stuff back to us
        final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<ExitNotification>();
        final AtomicReference<Object> lastResultHolder = new AtomicReference<Object>();

        // Whip up a tiny console executor that will execute shell command-lines
        Console.Executor executor = new Console.Executor() {
            public Result execute(final String line) throws Exception {
                assert line != null;

                try {
                    Object result = DefaultShell.this.execute(line);

                    lastResultHolder.set(result);
                }
                catch (ExitNotification n) {
                    exitNotifHolder.set(n);

                    return Result.STOP;
                }

                return Result.CONTINUE;
            }
        };

        IO io = getContext().getIo();
        
        // Setup the console runner
        JLineConsole console = new JLineConsole(executor, io);
        console.setPrompter(getPrompter());
        console.setErrorHandler(getErrorHandler());
        console.setHistory(getHistory());

        // Attach completers if there are any
        if (completers != null) {
            // Have to use aggregate here to get the completion list to update properly
            console.addCompleter(new AggregateCompleter(completers));
        }

        /*
        // Unless the user wants us to shut up, then display a nice welcome banner
        if (!io.isQuiet()) {
            String message = application.getModel().getBranding().getWelcomeMessage();
            if (message != null) {
                io.out.print(message);
                io.out.println(repeat("-", io.getTerminal().getTerminalWidth() - 1));
                io.out.flush();
            }
        }
        */

        // Check if there are args, and run them and then enter interactive
        if (args.length != 0) {
            execute(args);
        }

        // And then spin up the console and go for a jog
        console.run();

        // If any exit notification occured while running, then puke it up
        ExitNotification n = exitNotifHolder.get();
        if (n != null) {
            throw n;
        }
    }

    private static String repeat(final String str, final int repeat) {
        StringBuilder buffer = new StringBuilder(repeat * str.length());
        for (int i = 0; i < repeat; i++) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    public Console.Prompter getPrompter() {
        if (prompter == null) {
            throw new IllegalStateException("Missing configuration property: prompter");
        }
        return prompter;
    }

    public void setPrompter(final Console.Prompter prompter) {
        this.prompter = prompter;
    }

    public Console.ErrorHandler getErrorHandler() {
        if (errorHandler == null) {
            throw new IllegalStateException("Missing configuration property: errorHandler");
        }
        return errorHandler;
    }

    public void setErrorHandler(final Console.ErrorHandler handler) {
        this.errorHandler = handler;
    }

    //
    // Script Processing
    //

    private void loadProfileScripts() throws Exception {
        log.debug("Loading profile scripts");

        // Load profile scripts if they exist
        // loadSharedScript(application.getModel().getBranding().getProfileScriptName());
        // loadUserScript(application.getModel().getBranding().getProfileScriptName());
    }

    private void loadScript(final File file) throws Exception {
        assert file != null;

        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            String line;

            while ((line = reader.readLine()) != null) {
                execute(line);
            }
        }
        finally {
            Closer.close(reader);
        }
    }

    private void loadUserScript(final String fileName) throws Exception {
        assert fileName != null;

        /*
        File file = new File(application.getModel().getBranding().getUserDirectory(), fileName);

        if (file.exists()) {
            log.debug("Loading user-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("User script is not present: {}", file);
        }
        */
    }

    private void loadSharedScript(final String fileName) throws Exception {
        assert fileName != null;

        /*
        File file = new File(application.getModel().getBranding().getUserDirectory(), fileName);

        if (file.exists()) {
            log.debug("Loading shared-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("Shared script is not present: {}", file);
        }
        */
    }
}