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
import jline.History;
import org.apache.maven.shell.CommandExecutor;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.ShellContextHolder;
import org.apache.maven.shell.ansi.AnsiRenderer;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.console.Console;
import org.apache.maven.shell.console.JLineConsole;
import org.apache.maven.shell.io.Closer;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.notification.ExitNotification;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Component(role=Shell.class)
public class ShellImpl
    implements Shell, Initializable
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private CommandExecutor executor;

    @Requirement
    private History history;

    @Requirement(role=Completor.class, hints={"alias-name", "command-name"})
    private List<Completor> completers;

    @Requirement
    private Console.Prompter prompter;

    @Requirement
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

    public void initialize() throws InitializationException {
        try {
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
                    return ShellImpl.this;
                }

                public IO getIo() {
                    return io;
                }

                public Variables getVariables() {
                    return vars;
                }
            };

            // HACK: Need to resolve this in the new mvnsh context
            ShellContextHolder.set(context);

            vars.set("gshell.prompt", "@|bold,red maven|> ");

            // HACK: Add history for the 'history' command, since its not part of the Shell intf it can't really access it
            assert history != null;
            vars.set(SHELL_INTERNAL + History.class.getName(), history, true);

            loadProfileScripts();

            opened = true;
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

        loadUserScript("mvnsh.rc");

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

        assert prompter != null;
        console.setPrompter(prompter);

        assert errorHandler != null;
        console.setErrorHandler(errorHandler);

        assert history != null;
        console.setHistory(history);
        
        // Attach completers if there are any
        if (completers != null) {
            // Have to use aggregate here to get the completion list to update properly
            console.addCompleter(new AggregateCompleter(completers));
        }

        // Unless the user wants us to shut up, then display a nice welcome banner
        if (!io.isQuiet()) {
            io.out.println("@|bold,red Maven| Shell"); // TODO: Add mvn version here
            io.out.println(StringUtils.repeat("-", io.getTerminal().getTerminalWidth() - 1));
            io.out.flush();
        }

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
    
    //
    // Script Processing
    //

    private void loadProfileScripts() throws Exception {
        log.debug("Loading profile scripts");

        // Load profile scripts if they exist
        loadSharedScript("mvnsh.profile");
        loadUserScript("mvnsh.profile");
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

        // HACK: Need to use settings or something for this?
        File dir = new File(new File(System.getProperty("user.home")), ".m2");
        File file = new File(dir, fileName);

        if (file.exists()) {
            log.debug("Loading user-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("User script is not present: {}", file);
        }
    }

    private void loadSharedScript(final String fileName) throws Exception {
        assert fileName != null;

        // HACK: Need to use settings or something for this?
        File dir = new File("/etc/maven2");
        File file = new File(dir, fileName);

        if (file.exists()) {
            log.debug("Loading shared-script: {}", file);

            loadScript(file);
        }
        else {
            log.debug("Shared script is not present: {}", file);
        }
    }
}