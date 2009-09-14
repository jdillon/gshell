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

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.cli.Printer;
import org.apache.maven.shell.core.impl.registry.CommandRegistrationAgent;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceBundleMessageSource;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.IOHolder;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.terminal.AutoDetectedTerminal;
import org.apache.maven.shell.terminal.UnixTerminal;
import org.apache.maven.shell.terminal.UnsupportedTerminal;
import org.apache.maven.shell.terminal.WindowsTerminal;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Command-line bootstrap for Maven Shell.
 *
 * @version $Rev: 593403 $ $Date: 2007-11-09 09:43:59 +0700 (Fri, 09 Nov 2007) $
 */
public class Main
{
    private final ClassWorld classWorld;

    private final IO io = new IO();

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    public Main(final ClassWorld classWorld) {
        assert classWorld != null;

        this.classWorld = classWorld;
    }

    //
    // TODO: Add flag to capture output to log file
    //

    @Option(name="-h", aliases={"--help"}, requireOverride=true)
    private boolean help;

    @Option(name="-V", aliases={"--version"}, requireOverride=true)
    private boolean version;

    private void setConsoleLogLevel(final String level) {
        System.setProperty("mvnsh.log.console.level", level);
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

    @Argument(description="Command")
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

    @Option(name="-e", aliases={"--exception"})
    private void setException(boolean flag) {
        if (flag) {
            // FIXME: Stuff this into variables not properties
            System.setProperty("mvnsh.show.stacktrace","true");
        }
    }

    private PlexusContainer createContainer() throws PlexusContainerException {
        ContainerConfiguration config = new DefaultContainerConfiguration();
        config.setName("mvnsh.core");
        config.setClassWorld(classWorld);

        // FIXME: Hookup Plexus logging to Slf4j

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

        if (help) {
            io.out.println(System.getProperty("mvnsh.program") + " [options] <command> [args]");
            io.out.println();

            Printer printer = new Printer(clp);
            printer.setMessageSource(messages);
            printer.printUsage(io.out);

            io.out.println();
            io.out.flush();

            System.exit(ExitNotification.DEFAULT_CODE);
        }

        if (version) {
            io.out.println(System.getProperty("mvnsh.version"));
            io.out.println();
            io.out.flush();

            System.exit(ExitNotification.DEFAULT_CODE);
        }

        // Setup a refereence for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        int code = ExitNotification.DEFAULT_CODE;

        Runtime.getRuntime().addShutdownHook(new Thread("Shell Shutdown Hook") {
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
            PlexusContainer container = createContainer();

            IOHolder.set(io);

            // Boot up the shell instance
            Shell shell = container.lookup(Shell.class);

            // FIXME: Install default variables

            // Register our commands
            CommandRegistrationAgent agent = container.lookup(CommandRegistrationAgent.class);
            agent.registerCommands();
            
            // clp gives us a list, but we need an array
            String[] _args = {};
            if (commandArgs != null) {
                commandArgs.toArray(new String[commandArgs.size()]);
            }

            if (commands != null) {
                shell.execute(commands);
            }
            else {
                shell.run(_args);
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
        main(args, new ClassWorld("mvnsh.core", Thread.currentThread().getContextClassLoader()));
    }
}
