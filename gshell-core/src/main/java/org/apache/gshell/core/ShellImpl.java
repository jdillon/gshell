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

import jline.console.Completer;
import jline.console.ConsoleReader;
import org.apache.gshell.Branding;
import org.apache.gshell.History;
import org.apache.gshell.Shell;
import org.apache.gshell.ShellHolder;
import org.apache.gshell.VariableNames;
import org.apache.gshell.Variables;
import org.apache.gshell.console.Console;
import org.apache.gshell.core.console.ConsoleImpl;
import org.apache.gshell.core.console.HistoryImpl;
import org.apache.gshell.execute.CommandExecutor;
import org.apache.gshell.io.Closer;
import org.apache.gshell.io.IO;
import org.apache.gshell.io.SystemInputOutputHijacker;
import org.apache.gshell.notification.ExitNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The default {@link org.apache.gshell.Shell} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class ShellImpl
    implements Shell, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Branding branding;

    private final CommandExecutor executor;

    private final IO io;

    private final Variables variables;

    private final HistoryImpl history;

    private List<Completer> completers;

    private Console.Prompter prompter;

    private Console.ErrorHandler errorHandler;

    private boolean opened;
    
    public ShellImpl(final Branding branding, final CommandExecutor executor, final IO io, final Variables variables) throws IOException {
        assert branding != null;
        assert executor != null;
        // io and variables may be null

        this.branding = branding;
        this.executor = executor;
        this.io = io != null ? io : new IO();
        this.variables = variables != null ? variables : new Variables();
        this.history = new HistoryImpl(new File(branding.getUserContextDir(), branding.getHistoryFileName()));

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

    public List<Completer> getCompleters() {
        return completers;
    }

    public void setCompleters(final List<Completer> completers) {
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

        if (!SystemInputOutputHijacker.isInstalled()) {
            SystemInputOutputHijacker.install();
        }
        
        // Customize the shell
        branding.customize(this);

        // Load profile scripts
        loadProfileScripts();

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

        loadInteractiveScripts();

        // Setup 2 final refs to allow our executor to pass stuff back to us
        final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<ExitNotification>();
        final AtomicReference<Object> lastResultHolder = new AtomicReference<Object>();

        // Whip up a tiny console executor that will execute shell command-lines
        Console.Executor executor = new Console.Executor() {
            public Console.Result execute(final String line) throws Exception {
                assert line != null;

                try {
                    Object result = ShellImpl.this.execute(line);
                    lastResultHolder.set(result);
                    setLastResult(result);
                }
                catch (ExitNotification n) {
                    exitNotifHolder.set(n);
                    return Console.Result.STOP;
                }

                return Console.Result.CONTINUE;
            }
        };

        IO io = getIo();

        // Setup the console
        ConsoleImpl console = new ConsoleImpl(executor, io, history.getDelegate(), loadBindings());

        if (prompter != null) {
            console.setPrompter(prompter);
        }
        
        if (errorHandler != null) {
            console.setErrorHandler(errorHandler);
        }

        if (completers != null && !completers.isEmpty()) {
            for (Completer completer : completers) {
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
        File file = new File(branding.getUserContextDir(), ConsoleReader.JLINE_KEYBINDINGS);

        if (!file.exists() || !file.isFile()) {
            file = new File(branding.getShellContextDir(), ConsoleReader.JLINE_KEYBINDINGS);
            if (!file.exists() || file.isFile()) {
                try {
                    String fileName = System.getProperty(ConsoleReader.JLINE_KEYBINDINGS);
                    if (fileName != null) {
                        file = new File(fileName);
                    }
                    if (!file.exists() || file.isFile()) {
                        file = new File(branding.getUserHomeDir(), ConsoleReader.JLINEBINDINGS_PROPERTIES);
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to load keybindings", e);
                }
            }
        }

        InputStream bindings;
        
        if (file.exists() && file.isFile() && file.canRead()) {
            log.debug("Using bindings from file: {}", file);
            bindings = new BufferedInputStream(new FileInputStream(file));
        }
        else {
            log.trace("Using default bindings");
            bindings = io.getTerminal().getDefaultBindings();
        }

        return bindings;
    }

    protected void setLastResult(final Object result) {
        // result may be null
        getVariables().set(LAST_RESULT, result);
    }

    private void renderMessage(final IO io, final String msg) {
        assert io != null;
        if (msg != null) {
            io.out.println(msg);
            io.out.flush();
        }
    }

    protected void renderWelcomeMessage(final IO io) {
        renderMessage(io, branding.getWelcomeMessage());
    }

    protected void renderGoodbyeMessage(final IO io) {
        renderMessage(io, branding.getGoodbyeMessage());
    }

    // Script Loader

    protected void loadProfileScripts() throws Exception {
        String fileName = branding.getProfileScriptName();
        loadSharedScript(fileName);
        loadUserScript(fileName);
    }

    protected void loadInteractiveScripts() throws Exception {
        String fileName = branding.getInteractiveScriptName();
        loadSharedScript(fileName);
        loadUserScript(fileName);
    }

    protected void loadScript(final File file) throws Exception {
        assert file != null;

        log.debug("Loading script: {}", file);

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

    protected void loadUserScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(branding.getUserContextDir(), fileName);
        if (file.exists()) {
            loadScript(file);
        }
        else {
            log.trace("User script is not present: {}", file);
        }
    }

    protected void loadSharedScript(final String fileName) throws Exception {
        assert fileName != null;

        File file = new File(branding.getShellContextDir(), fileName);
        if (file.exists()) {
            loadScript(file);
        }
        else {
            log.trace("Shared script is not present: {}", file);
        }
    }
}