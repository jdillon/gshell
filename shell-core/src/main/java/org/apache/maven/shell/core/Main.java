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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.cli.Printer;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.terminal.UnsupportedTerminal;
import org.apache.maven.shell.terminal.WindowsTerminal;
import org.apache.maven.shell.terminal.UnixTerminal;
import org.apache.maven.shell.terminal.AutoDetectedTerminal;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.Command;
import org.apache.maven.shell.registry.CommandRegistry;

/**
 * Command-line bootstrap for Maven Shell.
 *
 * @version $Rev: 593403 $ $Date: 2007-11-09 09:43:59 +0700 (Fri, 09 Nov 2007) $
 */
public class Main
{
    ///CLOVER:OFF

    //
    // NOTE: Do not use logging from this class, as it is used to configure
    //       the logging level with System properties, which will only get
    //       picked up on the initial loading of Log4j
    //

    private final ClassWorld classWorld;

    private final IO io = new IO();

    public Main(final ClassWorld classWorld) {
        assert classWorld != null;

        this.classWorld = classWorld;
    }

    //
    // TODO: Add flag to capture output to log file
    //       https://issues.apache.org/jira/browse/GSHELL-47
    //

    @Option(name="-h", aliases={"--help"}, requireOverride=true, description="Display this help message")
    private boolean help;

    @Option(name="-V", aliases={"--version"}, requireOverride=true, description="Display program version")
    private boolean version;

    @Option(name="-i", aliases={"--interactive"}, description="Run in interactive mode")
    private boolean interactive = true;

    private void setConsoleLogLevel(final String level) {
        System.setProperty("gshell.log.console.level", level);
    }

    //
    // TODO: Add flag to show exception traces
    //       https://issues.apache.org/jira/browse/GSHELL-46
    //

    @Option(name="-d", aliases={"--debug"}, description="Enable DEBUG logging output")
    private void setDebug(boolean flag) {
        if (flag) {
            setConsoleLogLevel("DEBUG");
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Option(name="-v", aliases={"--verbose"}, description="Enable INFO logging output")
    private void setVerbose(boolean flag) {
        if (flag) {
            setConsoleLogLevel("INFO");
            io.setVerbosity(IO.Verbosity.VERBOSE);
        }
    }

    @Option(name="-q", aliases={"--quiet"}, description="Limit logging output to ERROR")
    private void setQuiet(boolean flag) {
        if (flag) {
            setConsoleLogLevel("ERROR");
            io.setVerbosity(IO.Verbosity.QUIET);
        }
    }

    @Option(name="-c", aliases={"--commands"}, description="Read commands from string")
    private String commands;

    @Argument(description="Command")
    private List<String> commandArgs = new ArrayList<String>(0);

    @Option(name="-D", aliases={"--define"}, token="NAME=VALUE", description="Define system properties")
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

    @Option(name="-C", aliases={"--color"}, argumentRequired=true, description="Enable or disable use of ANSI colors")
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

    private PlexusContainer createContainer() throws PlexusContainerException {
        // Boot up the container
        ContainerConfiguration config = new DefaultContainerConfiguration();
        config.setName("gshell.core");
        config.setClassWorld(classWorld);

        return new DefaultPlexusContainer(config);
    }

    public void boot(final String[] args) throws Exception {
        assert args != null;

        System.setProperty("jline.terminal", AutoDetectedTerminal.class.getName());

        // Default is to be quiet
        setConsoleLogLevel("WARN");

        CommandLineProcessor clp = new CommandLineProcessor(this);
        clp.setStopAtNonOption(true);
        clp.process(args);

        //
        // TODO: Use methods to handle these...
        //

        if (help) {
            io.out.println("mvnsh [options] <command> [args]");
            io.out.println();

            Printer printer = new Printer(clp);
            printer.printUsage(io.out);

            io.out.println();
            io.out.flush();

            System.exit(ExitNotification.DEFAULT_CODE);
        }

        if (version) {
            // FIXME: !!!
            io.out.println("FIXME: Version details unavailable ATM... sorry dude!"); // branding.getVersion());
            io.out.println();
            io.out.flush();

            System.exit(ExitNotification.DEFAULT_CODE);
        }

        // Setup a refereence for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        int code = ExitNotification.DEFAULT_CODE;

        Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Hook") {
            public void run() {
                if (codeRef.get() == null) {
                    // Give the user a warning when the JVM shutdown abnormally, normal shutdown
                    // will set an exit code through the proper channels

                    io.err.println();
                    io.err.println("WARNING: Abnormal JVM shutdown detected");
                }

                io.flush();
            }
        });

        try {
            PlexusContainer container = createContainer();

            // HACK: Wire up some commands to test with here for now
            CommandRegistry registry = container.lookup(CommandRegistry.class);
            registry.registerCommand(container.lookup(Command.class, "exit"));
            registry.registerCommand(container.lookup(Command.class, "alias"));
            registry.registerCommand(container.lookup(Command.class, "unalias"));

            // Boot up the shell instance
            Shell shell = container.lookup(Shell.class);

            // clp gives us a list, but we need an array
            String[] _args = commandArgs.toArray(new String[commandArgs.size()]);

            if (commands != null) {
                shell.execute(commands);
            }
            else if (interactive) {
                shell.run(_args);
            }
            else {
                shell.execute(_args);
            }
        }
        catch (ExitNotification n) {
            code = n.code;
        }

        codeRef.set(code);

        System.exit(code);
    }

    public static void main(final String[] args, final ClassWorld world) throws Exception {
        Main main = new Main(world);
        main.boot(args);
    }

    public static void main(final String[] args) throws Exception {
        main(args, new ClassWorld("gshell", Thread.currentThread().getContextClassLoader()));
    }
}
