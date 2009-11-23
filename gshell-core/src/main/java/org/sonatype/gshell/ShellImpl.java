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

package org.sonatype.gshell;

import jline.console.Completer;
import jline.console.ConsoleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.console.Console;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.console.ConsoleTask;
import org.sonatype.gshell.event.EventAware;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.io.StreamJack;
import org.sonatype.gshell.notification.ExitNotification;
import org.sonatype.gshell.util.Arguments;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The default {@link org.sonatype.gshell.Shell} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellImpl
    implements Shell, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Branding branding;

    private final CommandExecutor executor;

    private IO io;

    private final Variables variables;

    private final ShellHistory history;

    private List<Completer> completers;

    private ConsolePrompt prompt;

    private ConsoleErrorHandler errorHandler;

    private boolean opened;

    //
    // TODO: Maybe these should be set in variables?  More supportable than adding new methods for little features like this.
    //

    private boolean loadProfileScripts = true;

    private boolean loadInteractiveScripts = true;


    public ShellImpl(final EventManager eventManager, final CommandExecutor executor, final Branding branding,
                     final IO io, final Variables variables) throws IOException
    {
        assert eventManager != null;
        assert executor != null;
        assert branding != null;
        // io and variables may be null

        this.executor = executor;
        this.branding = branding;
        this.io = io != null ? io : new IO();
        this.variables = variables != null ? variables : new VariablesImpl();
        if (variables instanceof EventAware) {
            ((EventAware) variables).setEventManager(eventManager);
        }
        this.history = new ShellHistory(new File(branding.getUserContextDir(), branding.getHistoryFileName()));
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

    public void setPrompt(final ConsolePrompt prompt) {
        this.prompt = prompt;
    }

    public void setErrorHandler(final ConsoleErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setCompleters(final List<Completer> completers) {
        this.completers = completers;
    }

    public void setCompleters(final Completer... completers) {
        assert completers != null;
        setCompleters(Arrays.asList(completers));
    }

    public boolean isLoadProfileScripts() {
        return loadProfileScripts;
    }

    public void setLoadProfileScripts(boolean enable) {
        this.loadProfileScripts = enable;
    }

    public boolean isLoadInteractiveScripts() {
        return loadInteractiveScripts;
    }

    public void setLoadInteractiveScripts(boolean enable) {
        this.loadInteractiveScripts = enable;
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

        StreamJack.maybeInstall();

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

        log.debug("Starting interactive console; args: {}", Arguments.toStringArray(args));

        loadInteractiveScripts();

        // Setup 2 final refs to allow our executor to pass stuff back to us
        final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<ExitNotification>();
        final AtomicReference<Object> lastResultHolder = new AtomicReference<Object>();

        Callable<ConsoleTask> taskFactory = new Callable<ConsoleTask>() {
            public ConsoleTask call() throws Exception {
                return new ConsoleTask() {
                    @Override
                    public boolean doExecute(final String input) throws Exception {
                        try {
                            Object result = ShellImpl.this.execute(input);
                            lastResultHolder.set(result);
                            setLastResult(result);
                        }
                        catch (ExitNotification n) {
                            exitNotifHolder.set(n);
                            return false;
                        }

                        return true;
                    }
                };
            }
        };

        IO io = getIo();
        
        Console console = new Console(io, taskFactory, history, loadBindings());

        if (prompt != null) {
            console.setPrompt(prompt);
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

        // HACK: We have to replace the IO with the consoles so that children use the piped input
        final IO lastIo = io;
        this.io = console.getIo();

        final Shell lastShell = ShellHolder.set(this);
        
        try {
            console.run();
        }
        finally {
            this.io = lastIo;
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
                    log.warn("Failed to load key-bindings", e);
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
        if (!isLoadProfileScripts()) return;

        String fileName = branding.getProfileScriptName();
        loadSharedScript(fileName);
        loadUserScript(fileName);
    }

    protected void loadInteractiveScripts() throws Exception {
        if (!isLoadInteractiveScripts()) return;

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