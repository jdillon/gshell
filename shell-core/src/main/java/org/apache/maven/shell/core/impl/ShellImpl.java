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

package org.apache.maven.shell.core.impl;

import jline.Completor;
import org.apache.maven.shell.Branding;
import org.apache.maven.shell.History;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.console.Console;
import org.apache.maven.shell.core.impl.console.JLineConsole;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.terminal.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The default {@link Shell} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellImpl
    implements Shell, VariableNames, Constants
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Branding branding;

    private final CommandExecutor executor;

    private final IO io;

    private final Variables variables;

    private final JLineHistory history = new JLineHistory();

    private List<Completor> completers;

    private Console.Prompter prompter;

    private Console.ErrorHandler errorHandler;

    private boolean opened;
    
    public ShellImpl(final Branding branding, final CommandExecutor executor, final IO io, final Variables variables) {
        assert branding != null;
        assert executor != null;
        // io and variables may be null

        this.branding = branding;
        this.executor = executor;
        this.io = io != null ? io : new IO();
        this.variables = variables != null ? variables : new Variables();
    }

    public Branding getBranding() {
        return branding;
    }

    public IO getIo() {
        return io;
    }

    public Variables getVariables() {
        return variables;
    }

    public History getHistory() {
        return history;
    }

    public Console.Prompter getPrompter() {
        return prompter;
    }

    public void setPrompter(final Console.Prompter prompter) {
        this.prompter = prompter;
    }

    public Console.ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(final Console.ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public List<Completor> getCompleters() {
        return completers;
    }

    public void setCompleters(final List<Completor> completers) {
        this.completers = completers;
    }

    public synchronized boolean isOpened() {
        return opened;
    }

    public synchronized void close() {
        opened = false;
    }

    private synchronized void ensureOpened() {
        if (!opened) {
            try {
                open();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private synchronized void open() throws Exception {
        log.debug("Opening");

        //
        // TODO: Should we delegate all this to branding?
        //

        // Setup default variables
        if (!variables.contains(MVNSH_HOME)) {
            variables.set(MVNSH_HOME, branding.getShellHomeDir(), false);
        }
        if (!variables.contains(MVNSH_VERSION)) {
            variables.set(MVNSH_VERSION, branding.getVersion(), false);
        }
        if (!variables.contains(MVNSH_USER_HOME)) {
            variables.set(MVNSH_USER_HOME, branding.getUserHomeDir(), false);
        }
        if (!variables.contains(MVNSH_PROMPT)) {
            variables.set(MVNSH_PROMPT, branding.getDefaultPrompt());
        }

        // Configure history storage
        if (!variables.contains(MVNSH_HISTORY)) {
            File file = new File(branding.getUserContextDir(), branding.getHistoryFileName());
            history.setStoreFile(file);
            variables.set(MVNSH_HISTORY, file, false);
        }
        else {
            File file = new File(variables.get(MVNSH_HISTORY, String.class));
            history.setStoreFile(file);
        }

        // Setup context cwd
        if (!variables.contains(MVNSH_USER_DIR)) {
            variables.set(MVNSH_USER_DIR, new File(".").getCanonicalPath());
        }

        // Load profile scripts
        new ScriptLoader(this).loadProfileScripts();

        opened = true;

        log.debug("Opened");
    }

    public boolean isInteractive() {
        return true;
    }

    // FIXME: History should still be appended if not running inside of a JLineConsole
    
    public Object execute(final String line) throws Exception {
        ensureOpened();
        return executor.execute(this, line);
    }

    public Object execute(final String command, final Object[] args) throws Exception {
        ensureOpened();
        return executor.execute(this, command, args);
    }

    public Object execute(final Object... args) throws Exception {
        ensureOpened();
        return executor.execute(this, args);
    }

    public void run(final Object... args) throws Exception {
        assert args != null;
        ensureOpened();

        log.debug("Starting interactive console; args: {}", args);

        new ScriptLoader(this).loadInteractiveScripts();

        // Setup 2 final refs to allow our executor to pass stuff back to us
        final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<ExitNotification>();
        final AtomicReference<Object> lastResultHolder = new AtomicReference<Object>();

        // Whip up a tiny console executor that will execute shell command-lines
        Console.Executor executor = new Console.Executor() {
            public Result execute(final String line) throws Exception {
                assert line != null;

                try {
                    Object result = ShellImpl.this.execute(line);
                    lastResultHolder.set(result);
                    setLastResult(result);
                }
                catch (ExitNotification n) {
                    exitNotifHolder.set(n);
                    return Result.STOP;
                }

                return Result.CONTINUE;
            }
        };

        IO io = getIo();

        // Setup the console
        JLineConsole console = new JLineConsole(executor, io, loadBindings());
        console.setHistory(history.getDelegate());

        if (prompter != null) {
            console.setPrompter(prompter);
        }
        
        if (errorHandler != null) {
            console.setErrorHandler(errorHandler);
        }

        if (completers != null && !completers.isEmpty()) {
            for (Completor completer : completers) {
                console.addCompleter(completer);
            }
        }

        if (!io.isQuiet()) {
            renderWelcomeMessage(io);
        }

        // Check if there are args, and run them and then enter interactive
        if (args.length != 0) {
            execute(args);
        }

        final Shell lastShell = ShellHolder.set(this);
        try {
            console.run();
        }
        finally {
            ShellHolder.set(lastShell);
        }

        if (!io.isQuiet()) {
            renderGoodbyeMessage(io);
        }

        // If any exit notification occurred while running, then puke it up
        ExitNotification n = exitNotifHolder.get();
        if (n != null) {
            throw n;
        }
    }

    private InputStream loadBindings() throws IOException {
        File file = new File(branding.getUserContextDir(), JLINE_KEYBINDINGS);

        if (!file.exists() || !file.isFile()) {
            file = new File(branding.getShellContextDir(), JLINE_KEYBINDINGS);
            if (!file.exists() || file.isFile()) {
                try {
                    String fileName = System.getProperty(JLINE_KEYBINDINGS);
                    if (fileName != null) {
                        file = new File(fileName);
                    }
                    if (!file.exists() || file.isFile()) {
                        file = new File(System.getProperty("user.home", JLINEBINDINGS_PROPERTIES));
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to load keybindings", e);
                }
            }
        }

        InputStream bindings = null;
        
        if (file.exists() && file.isFile() && file.canRead()) {
            log.debug("Using bindings from file: {}", file);
            bindings = new BufferedInputStream(new FileInputStream(file));
        }
        else {
            log.debug("Using default bindings");
            bindings = io.getTerminal().getDefaultBindings();
        }

        return bindings;
    }

    protected void setLastResult(final Object result) {
        // result may be null
        getVariables().set(LAST_RESULT, result);
    }

    protected void renderGoodbyeMessage(final IO io) {
        assert io != null;
        String msg = branding.getGoodbyeMessage();
        if (msg != null) {
            io.out.println(msg);
            io.out.flush();
        }
    }

    protected void renderWelcomeMessage(final IO io) {
        assert io != null;
        String msg = branding.getWelcomeMessage();
        if (msg != null) {
            io.out.print(msg);
            io.out.flush();
        }
    }
}