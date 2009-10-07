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

import jline.TerminalFactory;
import org.apache.gshell.Branding;
import org.apache.gshell.Shell;
import org.apache.gshell.VariableNames;
import org.apache.gshell.Variables;
import org.apache.gshell.ansi.Ansi;
import org.apache.gshell.ansi.AnsiIO;
import org.apache.gshell.cli.Argument;
import org.apache.gshell.cli.NameValue;
import org.apache.gshell.cli.Option;
import org.apache.gshell.cli.Printer;
import org.apache.gshell.cli.ProcessingException;
import org.apache.gshell.cli.Processor;
import org.apache.gshell.cli.handler.StopHandler;
import org.apache.gshell.i18n.MessageSource;
import org.apache.gshell.i18n.ResourceBundleMessageSource;
import org.apache.gshell.internal.Log;
import org.apache.gshell.io.IO;
import org.apache.gshell.notification.ExitNotification;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Support for booting shell applications.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public abstract class MainSupport
    implements VariableNames
{
    protected final IO io = new AnsiIO();

    protected final Variables vars = new Variables();

    protected final MessageSource messages = new ResourceBundleMessageSource()
            .add(false, getClass())
            .add(MainSupport.class);

    private Branding branding;

    //
    // TODO: Add flag to capture output to log file
    //

    @Option(name="-h", aliases={"--help"}, requireOverride=true)
    protected boolean help;

    @Option(name="--", handler= StopHandler.class)
    private boolean stop;

    @Option(name="-V", aliases={"--version"}, requireOverride=true)
    protected boolean version;

    @Option(name="-e", aliases={"--errors"})
    protected boolean showErrorTraces = false;

    protected void setConsoleLogLevel(final Log.Level level) {
        System.setProperty(SHELL_LOGGING, level.name());
        vars.set(SHELL_LOGGING, level);
    }

    @Option(name="-d", aliases={"--debug"})
    protected void setDebug(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Log.Level.DEBUG);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-X", aliases={"--trace"})
    protected void setTrace(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Log.Level.TRACE);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-v", aliases={"--verbose"})
    protected void setVerbose(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Log.Level.INFO);
            io.setVerbosity(IO.Verbosity.VERBOSE);
        }
    }

    @Option(name="-q", aliases={"--quiet"})
    protected void setQuiet(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Log.Level.ERROR);
            io.setVerbosity(IO.Verbosity.QUIET);
        }
    }

    @Option(name="-c", aliases={"--command"}, argumentRequired=true)
    protected String command;

    @Option(name="-D", aliases={"--define"}, argumentRequired=true)
    protected void setVariable(final String input) {
        NameValue nv = NameValue.parse(input);
        vars.set(nv.name, nv.value);
    }

    @Option(name="-P", aliases={"--property"}, argumentRequired=true)
    protected void setSystemProperty(final String input) {
        NameValue nv = NameValue.parse(input);
        System.setProperty(nv.name, nv.value);
    }

    @Option(name="-C", aliases={"--color"}, argumentRequired=true)
    protected void enableAnsiColors(final boolean flag) {
        Ansi.setEnabled(flag);
    }

    @Option(name="-T", aliases={"--terminal"}, argumentRequired=true)
    protected void setTerminalType(final String type) {
        TerminalFactory.configure(type);
    }
    protected void setTerminalType(final TerminalFactory.Type type) {
        TerminalFactory.configure(type);
    }

    @Argument
    protected List<String> appArgs = null;

    protected void exit(final int code) {
        io.flush();
        System.exit(code);
    }

    protected Branding getBranding() throws Exception {
        if (branding == null) {
            branding = createBranding();
        }
        return branding;
    }

    public void boot(final String... args) throws Exception {
        assert args != null;

        Log.debug("Booting w/args: ", args);

        // Setup environment defaults
        // Ansi.install();
        setConsoleLogLevel(Log.Level.WARN);
        setTerminalType(TerminalFactory.Type.AUTO);

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
            printer.printUsage(io.out, getBranding().getProgramName());
            exit(ExitNotification.DEFAULT_CODE);
        }

        if (version) {
            io.out.format("%s %s", getBranding().getDisplayName(), getBranding().getVersion()).println();
            exit(ExitNotification.DEFAULT_CODE);
        }

        // Setup a reference for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        int code = ExitNotification.DEFAULT_CODE;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
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
            vars.set(SHELL_ERRORS, showErrorTraces);

            Shell shell = createShell();

            if (command != null) {
                shell.execute(command);
            }
            else {
                shell.run(appArgs != null ? appArgs.toArray() : new Object[0]);
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

    protected abstract Branding createBranding() throws Exception;
    
    protected abstract Shell createShell() throws Exception;
}