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

package org.apache.maven.shell.core;

import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceBundleMessageSource;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.terminal.AutoDetectedTerminal;
import org.apache.maven.shell.terminal.UnixTerminal;
import org.apache.maven.shell.terminal.WindowsTerminal;
import org.apache.maven.shell.terminal.UnsupportedTerminal;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Command-line bootstrap for Maven Shell.
 *
 * @version $Rev$ $Date$
 */
public class Main
{
    private static final boolean BYPASS_EXIT = Boolean.getBoolean(Main.class.getName() + ".bypassExit");

    //
    // NOTE: Do not use logging from this class, as it is used to configure
    //       the logging level with System properties, which will only get
    //       picked up on the initial loading of Log4j
    //

    private final IO io = new IO();

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    //
    // TODO: Add flag to capture output to log file
    //       https://issues.apache.org/jira/browse/GSHELL-47
    //

    //
    // TODO: Add --file <file>, which will run: source <file>
    //

    //
    // FIXME: Really need to allow the location of the application.xml to be passed in!
    //

    @Option(name="-h", aliases={"--help"}, requireOverride=true)
    private boolean help;

    @Option(name="-V", aliases={"--version"}, requireOverride=true)
    private boolean version;

    @Option(name="-i", aliases={"--interactive"})
    private boolean interactive = true;

    private void setConsoleLogLevel(final String level) {
        System.setProperty("gshell.log.console.level", level);
    }

    @Option(name="-e", aliases={"--exception"})
    private void setException(boolean flag) {
        if (flag) {
            System.setProperty("gshell.show.stacktrace","true");
        }
    }

    @Option(name="-d", aliases={"--debug"})
    private void setDebug(boolean flag) {
        if (flag) {
            setConsoleLogLevel("DEBUG");
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-X", aliases={"--trace"})
    private void setTrace(boolean flag) {
        if (flag) {
            setConsoleLogLevel("TRACE");
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-v", aliases={"--verbose"})
    private void setVerbose(boolean flag) {
        if (flag) {
            setConsoleLogLevel("INFO");
            io.setVerbosity(IO.Verbosity.VERBOSE);
        }
    }

    @Option(name="-q", aliases={"--quiet"})
    private void setQuiet(boolean flag) {
        if (flag) {
            setConsoleLogLevel("ERROR");
            io.setVerbosity(IO.Verbosity.QUIET);
        }
    }

    @Option(name="-c", aliases={"--commands"})
    private String commands;

    @Argument
    private List<String> commandArgs = null;

    @Option(name="-D", aliases={"--define"})
    private void setSystemProperty(final String nameValue) {
        assert nameValue != null;

        String name, value;
        int i = nameValue.indexOf("=");

        if (i == -1) {
            name = nameValue;
            value = Boolean.TRUE.toString();
        }
        else {
            name = nameValue.substring(0, i);
            value = nameValue.substring(i + 1, nameValue.length());
        }
        name = name.trim();

        System.setProperty(name, value);
    }

    @Option(name="-C", aliases={"--color"}, argumentRequired=true)
    private void enableAnsiColors(final boolean flag) {
        Ansi.setEnabled(flag);
    }

    @Option(name="-T", aliases={"--terminal"}, argumentRequired=true)
    private void setTerminalType(String type) {
        type = type.toLowerCase();

        if ("auto".equals(type)) {
            type = AutoDetectedTerminal.class.getName();
        }
        else if ("unix".equals(type)) {
            type = UnixTerminal.class.getName();
        }
        else if ("win".equals(type) || "windows".equals("type")) {
            type = WindowsTerminal.class.getName();
        }
        else if ("false".equals(type) || "off".equals(type) || "none".equals(type)) {
            type = UnsupportedTerminal.class.getName();
        }

        System.setProperty("jline.terminal", type);
    }

    public int boot(final String[] args) throws Exception {
        assert args != null;

        System.setProperty("jline.terminal", AutoDetectedTerminal.class.getName());

        // Default is to be quiet
        setConsoleLogLevel("WARN");

        CommandLineProcessor clp = new CommandLineProcessor(this);
        clp.setStopAtNonOption(true);
        clp.process(args);

        // Setup a refereence for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        int code = ExitNotification.DEFAULT_CODE;

        Runtime.getRuntime().addShutdownHook(new Thread("GShell Shutdown Hook") {
            public void run() {
                if (codeRef.get() == null) {
                    // Give the user a warning when the JVM shutdown abnormally, normal shutdown
                    // will set an exit code through the proper channels

                    if (!io.isSilent()) {
                        io.err.println();
                        io.err.println(messages.getMessage("warning.abnormalShutdown"));
                    }
                }

                io.flush();
            }
        });

        try {
            System.out.println("HI");
            
            /*
            ShellBuilder builder = new ShellBuilderImpl();
            builder.setClassLoader(getClass().getClassLoader());
            builder.setIo(io);

            if (!io.isQuiet() && !io.isSilent()) {
                // Configure the download monitor
                ArtifactResolver artifactResolver = builder.getContainer().getBean(ArtifactResolver.class);
                artifactResolver.setTransferListener(new ProgressSpinnerMonitor(io));
            }

            // --help and --version need access to the application's information, so we have to handle these options late
            if (help|version) {
                ApplicationModel applicationModel = builder.getApplicationModel();

                if (help) {
                    Printer printer = new Printer(clp);
                    printer.setMessageSource(messages);
                    printer.printUsage(io.out, applicationModel.getBranding().getProgramName());
                }
                else if (version) {
                    io.out.println(applicationModel.getVersion());
                }

                io.out.flush();

                throw new ExitNotification();
            }

            // Build the shell instance
            Shell gshell = builder.create();

            // clp gives us a list, but we need an array
            String[] _args = {};
            if (commandArgs != null) {
                _args = commandArgs.toArray(new String[commandArgs.size()]);
            }

            if (commands != null) {
                gshell.execute(commands);
            }
            else if (interactive) {
                gshell.run(_args);
            }
            else {
                gshell.execute(_args);
            }
            */
        }
        catch (ExitNotification n) {
            code = n.code;
        }

        codeRef.set(code);

        return code;
    }

    public static void main(final String[] args) throws Exception {
        Main main = new Main();

        int code = main.boot(args);

        if (!BYPASS_EXIT) {
            System.exit(code);
        }
    }
}

