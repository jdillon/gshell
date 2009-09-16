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
import org.apache.maven.shell.History;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.ShellContextHolder;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.console.Console;
import org.apache.maven.shell.console.JLineConsole;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.notification.ExitNotification;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The default {@link Shell} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Shell.class)
public class ShellImpl
    implements Shell, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private CommandExecutor executor;

    @Requirement(role=Completor.class, hints={"alias-name", "commands"})
    private List<Completor> completers;

    @Requirement
    private Console.Prompter prompter;

    @Requirement
    private Console.ErrorHandler errorHandler;

    private IO io = new IO();

    private Variables variables = new Variables();

    private JLineHistory history = new JLineHistory();

    private ScriptLoader scriptLoader = new ScriptLoader(this);

    private boolean opened;

    public IO getIo() {
        return io;
    }

    public void setIo(final IO io) {
        assert io != null;
        this.io = io;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(final Variables variables) {
        assert variables != null;
        this.variables = variables;
    }

    public History getHistory() {
        return history;
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

        // setUp default variables
        if (!variables.contains(MVNSH_HOME)) {
            variables.set(MVNSH_HOME, System.getProperty(MVNSH_HOME), false);
        }
        if (!variables.contains(MVNSH_VERSION)) {
            variables.set(MVNSH_VERSION, System.getProperty(MVNSH_VERSION), false);
        }
        if (!variables.contains(MVNSH_USER_HOME)) {
            variables.set(MVNSH_USER_HOME, System.getProperty("user.home"), false);
        }
        if (!variables.contains(MVNSH_USER_DIR)) {
            variables.set(MVNSH_USER_DIR, System.getProperty("user.dir"));
        }
        if (!variables.contains(MVNSH_PROMPT)) {
            variables.set(MVNSH_PROMPT, "@|bold mvnsh|:%{" + MVNSH_USER_DIR + "}> ");
        }

        // Configure history storage
        if (!variables.contains(MVNSH_HISTORY)) {
            File dir = new File(variables.get(MVNSH_USER_HOME, String.class), ".m2");
            File file = new File(dir, MVNSH_HISTORY);
            history.setStoreFile(file);
            variables.set(MVNSH_HISTORY, file.getCanonicalFile(), false);
        }
        else {
            File file = new File(variables.get(MVNSH_HISTORY, String.class));
            history.setStoreFile(file);
        }
        
        // Load profile scripts
        scriptLoader.loadProfileScripts();

        opened = true;

        log.debug("Opened");
    }


    public boolean isInteractive() {
        return true;
    }

    private ShellContext createShellContext() {
        ShellContext context = new ShellContext()
        {
            public Shell getShell() {
                return ShellImpl.this;
            }

            public IO getIo() {
                return ShellImpl.this.getIo();
            }

            public Variables getVariables() {
                return ShellImpl.this.getVariables();
            }
        };

        log.debug("Created shell context: {}", context);

        ShellContextHolder.set(context);

        return context;
    }

    // FIXME: History should still be appended if not running inside of a JLineConsole
    
    public Object execute(final String line) throws Exception {
        ensureOpened();

        return executor.execute(createShellContext(), line);
    }

    public Object execute(final String command, final Object[] args) throws Exception {
        ensureOpened();

        return executor.execute(createShellContext(), command, args);
    }

    public Object execute(final Object... args) throws Exception {
        ensureOpened();

        return executor.execute(createShellContext(), args);
    }

    public void run(final Object... args) throws Exception {
        assert args != null;

        ensureOpened();

        log.debug("Starting interactive console; args: {}", args);

        scriptLoader.loadInteractiveScripts();

        // setUp 2 final refs to allow our executor to pass stuff back to us
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

        IO io = getIo();
        
        // setUp the console runner
        JLineConsole console = new JLineConsole(executor, io);
        console.setHistory(history.getDelegate());

        assert prompter != null;
        console.setPrompter(prompter);

        assert errorHandler != null;
        console.setErrorHandler(errorHandler);

        // Attach completers if there are any
        if (completers != null) {
            // Have to use aggregate here to get the completion list to update properly
            console.addCompleter(new AggregateCompleter(completers));
        }

        // Unless the user wants us to shut up, then display a nice welcome banner
        if (!io.isQuiet()) {
            io.out.println("@|bold,red Apache Maven| @|bold Shell|");
            io.out.println(StringUtils.repeat("-", io.getTerminal().getTerminalWidth() - 1));
            io.out.flush();
        }

        // Check if there are args, and run them and then enter interactive
        if (args.length != 0) {
            execute(args);
        }

        // And then spin up the console and go for a jog
        console.run();

        // If any exit notification occurred while running, then puke it up
        ExitNotification n = exitNotifHolder.get();
        if (n != null) {
            throw n;
        }
    }
}