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

package org.apache.maven.shell;

import jline.Completor;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.cli.Printer;
import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.cli.Processor;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.core.ShellBuilder;
import org.apache.maven.shell.MvnshBranding;
import org.apache.maven.shell.core.impl.console.ConsoleErrorHandlerImpl;
import org.apache.maven.shell.core.impl.console.ConsolePrompterImpl;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceBundleMessageSource;
import org.apache.maven.shell.io.AnsiAwareIO;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.terminal.AutoDetectedTerminal;
import org.codehaus.plexus.PlexusContainer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Command-line bootstrap for Apache Maven Shell (<tt>shell</tt>).
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Main
    implements VariableNames
{
    private static final String DEBUG = "DEBUG";

    private static final String TRACE = "TRACE";

    private static final String INFO = "INFO";

    private static final String ERROR = "ERROR";

    private static final String WARN = "WARN";

    private final IO io = new AnsiAwareIO();

    private final Variables vars = new Variables();

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    //
    // TODO: Add flag to capture output to log file
    //

    @Option(name="-h", aliases={"--help"}, requireOverride=true)
    private boolean help;

    @Option(name="-V", aliases={"--version"}, requireOverride=true)
    private boolean version;

    @Option(name="-e", aliases={"--errors"})
    private boolean showErrorTraces = false;

    private void setConsoleLogLevel(final String level) {
        System.setProperty(SHELL_LOGGING, level);
        vars.set(SHELL_LOGGING, level);
    }

    @Option(name="-d", aliases={"--debug"})
    private void setDebug(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(DEBUG);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-X", aliases={"--trace"})
    private void setTrace(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(TRACE);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-v", aliases={"--verbose"})
    private void setVerbose(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(INFO);
            io.setVerbosity(IO.Verbosity.VERBOSE);
        }
    }

    @Option(name="-q", aliases={"--quiet"})
    private void setQuiet(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(ERROR);
            io.setVerbosity(IO.Verbosity.QUIET);
        }
    }

    @Option(name="-c", aliases={"--commands"})
    private String commands;

    @Argument()
    private List<String> commandArgs = null;

    @Option(name="-D", aliases={"--define"})
    private void setVariable(final String input) {
        NameValue nv = NameValue.parse(input);
        vars.set(nv.name, nv.value);
    }

    @Option(name="-P", aliases={"--property"})
    private void setSystemProperty(final String input) {
        NameValue nv = NameValue.parse(input);
        System.setProperty(nv.name, nv.value);
    }

    @Option(name="-C", aliases={"--color"}, argumentRequired=true)
    private void enableAnsiColors(final boolean flag) {
        Ansi.setEnabled(flag);
    }

    @Option(name="-T", aliases={"--terminal"}, argumentRequired=true)
    private void setTerminalType(final String type) {
        AutoDetectedTerminal.configure(type);
    }

    private void exit(final int code) {
        io.flush();
        System.exit(code);
    }

    public void boot(final String[] args) throws Exception {
        assert args != null;

        Branding branding = new MvnshBranding();

        // Setup environment defaults
        setTerminalType(AutoDetectedTerminal.AUTO);
        setConsoleLogLevel(WARN);

        // Process command line options & arguments
        Processor clp = new Processor(this);
        clp.setMessages(messages);
        clp.setStopAtNonOption(true);

        try {
            clp.process(args);
        }
        catch (ProcessingException e) {
            if (showErrorTraces) {
                e.printStackTrace(io.err);
            }
            else {
                io.err.println(e);
            }
            exit(ExitNotification.FATAL_CODE);
        }

        if (help) {
            Printer printer = new Printer(clp);
            printer.printUsage(io.out, branding.getProgramName());
            exit(ExitNotification.DEFAULT_CODE);
        }

        if (version) {
            io.out.format("%s %s", branding.getDisplayName(), branding.getVersion()).println();
            exit(ExitNotification.DEFAULT_CODE);
        }

        // Setup a reference for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        int code = ExitNotification.DEFAULT_CODE;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (codeRef.get() == null) {
                    // Give the user a warning when the JVM shutdown abnormally, normal shutdown
                    // will set an exit code through the proper channels

                    io.err.println();
                    io.err.println(messages.getMessage("warning.abnormalShutdown"));
                }

                io.flush();
            }
        });

        try {
            // Create the container instance, we need it to look up some components to configure the shell
            PlexusContainer container = ShellBuilder.createContainer();

            vars.set(SHELL_ERRORS, showErrorTraces);

            // Build a shell instance
            Shell shell = new ShellBuilder()
                    .setContainer(container)
                    .setBranding(branding)
                    .setIo(io)
                    .setVariables(vars)
                    .setPrompter(new ConsolePrompterImpl(vars, branding))
                    .setErrorHandler(new ConsoleErrorHandlerImpl(io))
                    .addCompleter(new AggregateCompleter(
                            container.lookup(Completor.class, "alias-name"),
                            container.lookup(Completor.class, "commands")
                    ))
                    .create();

            // clp gives us a list, but we need an array
            String[] _args = {};
            if (commandArgs != null) {
                commandArgs.toArray(new String[commandArgs.size()]);
            }

            if (commands != null) {
                shell.execute(commands);
            }
            else {
                shell.run((Object[])_args);
            }
        }
        catch (ExitNotification n) {
            code = n.code;
        }
        finally {
            io.flush();
        }

        codeRef.set(code);

        exit(code);
    }

    public static void main(final String[] args) throws Exception {
        new Main().boot(args);
    }
}
