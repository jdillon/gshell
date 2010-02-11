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

import jline.AnsiWindowsTerminal;
import jline.NoInterruptUnixTerminal;
import jline.TerminalFactory;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.sonatype.gossip.Level;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.io.StreamJack;
import org.sonatype.gshell.io.StreamSet;
import org.sonatype.gshell.notification.ExitNotification;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.util.Arguments;
import org.sonatype.gshell.util.NameValue;
import org.sonatype.gshell.util.ansi.AnsiIO;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.HelpPrinter;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.util.pref.Preference;
import org.sonatype.gshell.util.pref.PreferenceProcessor;
import org.sonatype.gshell.util.pref.Preferences;
import org.sonatype.gshell.vars.Variables;
import org.sonatype.gshell.vars.VariablesImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static org.sonatype.gshell.vars.VariableNames.*;

/**
 * Support for booting shell applications.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Preferences(path = "cli")
public abstract class MainSupport
{
    static {
        // Register some different terminal flavors for added functionality
        TerminalFactory.registerFlavor(TerminalFactory.Flavor.UNIX, NoInterruptUnixTerminal.class);
        TerminalFactory.registerFlavor(TerminalFactory.Flavor.WINDOWS, AnsiWindowsTerminal.class);

        // Register jline ansi detector
        Ansi.setDetector(new Callable<Boolean>()
        {
            public Boolean call() throws Exception {
                return TerminalFactory.get().isAnsiSupported();
            }
        });
    }

    protected final Logger log = Log.getLogger(MainSupport.class);

    protected final IO io = new AnsiIO(StreamSet.SYSTEM_FD, true);

    protected final Variables vars = new VariablesImpl();

    protected final MessageSource messages = new ResourceBundleMessageSource()
        .add(false, getClass())
        .add(MainSupport.class);

    private Branding branding;

    //
    // TODO: Add flag to capture output to log file. Need to bring back TeeOutputStream ans
    //       get prefixed stuff working proper.
    //

    // TODO: Add --batch flag (for non-interactive)

    @Option(name = "h", longName = "help", override = true)
    protected boolean help;

    @Option(name = "V", longName = "version", override = true)
    protected boolean version;

    @Preference
    @Option(name = "e", longName = "errors")
    protected boolean showErrorTraces = false;

    protected void setConsoleLogLevel(final Level level) {
        System.setProperty(SHELL_LOGGING, level.name());
        vars.set(SHELL_LOGGING, level);
    }

    @Preference(name = "debug")
    @Option(name = "d", longName = "debug")
    protected void setDebug(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Level.DEBUG);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Preference(name = "trace")
    @Option(name = "X", longName = "trace")
    protected void setTrace(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Level.TRACE);
            io.setVerbosity(IO.Verbosity.DEBUG);
        }
    }

    @Preference(name = "verbose")
    @Option(name = "v", longName = "verbose")
    protected void setVerbose(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Level.INFO);
            io.setVerbosity(IO.Verbosity.VERBOSE);
        }
    }

    @Preference(name = "quiet")
    @Option(name = "q", longName = "quiet")
    protected void setQuiet(final boolean flag) {
        if (flag) {
            setConsoleLogLevel(Level.ERROR);
            io.setVerbosity(IO.Verbosity.QUIET);
        }
    }

    @Option(name = "c", longName = "command")
    protected String command;

    @Option(name = "D", longName = "define")
    protected void setVariable(final String input) {
        NameValue nv = NameValue.parse(input);
        vars.set(nv.name, nv.value);
    }

    @Option(name = "P", longName = "property")
    protected void setSystemProperty(final String input) {
        NameValue nv = NameValue.parse(input);
        System.setProperty(nv.name, nv.value);
    }

    @Preference(name = "color")
    @Option(name = "C", longName = "color")
    protected void enableAnsiColors(final Boolean flag) {
        Ansi.setEnabled(flag);
    }

    @Preference(name = "terminal")
    @Option(name = "T", longName = "terminal")
    protected void setTerminalType(final String type) {
        TerminalFactory.configure(type);
    }

    protected void setTerminalType(final TerminalFactory.Type type) {
        TerminalFactory.configure(type);
    }

    // TODO: Add --norc && --noprofile

    @Argument()
    protected List<String> appArgs = null;

    protected void exit(final int code) {
        io.flush();
        System.exit(code);
    }

    protected Branding getBranding() {
        if (branding == null) {
            branding = createBranding();
        }
        return branding;
    }

    public void boot(String... args) throws Exception {
        assert args != null;

        args = Arguments.clean(args);
        log.debug("Booting w/args: {}", Arrays.asList(args));

        // Setup environment defaults
        setConsoleLogLevel(Level.WARN);
        setTerminalType(TerminalFactory.Type.AUTO);

        // Process preferences
        PreferenceProcessor pp = new PreferenceProcessor();
        pp.setBasePath(getBranding().getPreferencesBasePath());
        pp.addBean(this);
        pp.process();

        // Process command line options & arguments
        CliProcessor clp = new CliProcessor();
        clp.addBean(this);
        clp.setMessages(messages);
        clp.setStopAtNonOption(true);

        try {
            clp.process(args);
        }
        catch (Exception e) {
            if (showErrorTraces) {
                e.printStackTrace(io.err);
            }
            else {
                io.err.println(e);
            }
            exit(ExitNotification.FATAL_CODE);
        }

        if (help) {
            HelpPrinter printer = new HelpPrinter(clp);
            printer.printUsage(io.out, getBranding().getProgramName());
            exit(ExitNotification.DEFAULT_CODE);
        }

        if (version) {
            io.out.format("%s %s", getBranding().getDisplayName(), getBranding().getVersion()).println();
            exit(ExitNotification.DEFAULT_CODE);
        }

        StreamJack.maybeInstall(io.streams);

        // Setup a reference for our exit code so our callback thread can tell if we've shutdown normally or not
        final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
        Object result = null;

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
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
                result = shell.execute(command);
            }
            else if (appArgs != null) {
                result = shell.execute(appArgs.toArray());
            }
            else {
                shell.run();
            }
        }
        catch (ExitNotification n) {
            result = n.code;
        }
        finally {
            io.flush();
        }

        if (result == null) {
            result = vars.get(LAST_RESULT);
        }

        int code;

        // TODO: Support parsing strings for exit code.  Move this to a helper class
        if (result instanceof CommandAction.Result) {
            code = ((CommandAction.Result) result).ordinal();
        }
        else if (result instanceof Number) {
            code = ((Number) result).intValue();
        }
        else if (result == null) {
            code = 0;
        }
        else {
            code = 1;
        }

        codeRef.set(code);

        exit(code);
    }

    protected abstract Branding createBranding();

    protected abstract Shell createShell() throws Exception;
}