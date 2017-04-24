/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.annotations.VisibleForTesting;
import com.planet57.gossip.Level;
import com.planet57.gossip.Log;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.AnsiIO;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.execute.ExitNotification;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.Arguments;
import com.planet57.gshell.util.NameValue;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.util.io.StreamSet;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.util.pref.Preferences;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for booting shell applications.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Preferences(path = "cli")
public abstract class MainSupport
{
  // Gossip Log used here for bootstrap
  protected final Logger log = Log.getLogger(getClass());

  private final MessageSource messages = new ResourceBundleMessageSource()
    .add(false, getClass())
    .add(MainSupport.class);

  protected IO io;

  protected Variables vars;

  private Branding branding;

  @Option(name = "h", longName = "help", override = true)
  protected boolean help;

  @Option(name = "V", longName = "version", override = true)
  protected boolean version;

  @Preference
  @Option(name = "e", longName = "errors", optionalArg = true)
  protected boolean showErrorTraces = false;

  protected void setConsoleLogLevel(final Level level) {
    System.setProperty(VariableNames.SHELL_LOGGING, level.name());
    vars.set(VariableNames.SHELL_LOGGING, level);
  }

  @Preference(name = "debug")
  @Option(name = "d", longName = "debug", optionalArg = true)
  protected void setDebug(final boolean flag) {
    if (flag) {
      setConsoleLogLevel(Level.DEBUG);
      io.setVerbosity(IO.Verbosity.NORMAL);
      showErrorTraces = true;
    }
  }

  @Preference(name = "trace")
  @Option(name = "X", longName = "trace", optionalArg = true)
  protected void setTrace(final boolean flag) {
    if (flag) {
      setConsoleLogLevel(Level.TRACE);
      io.setVerbosity(IO.Verbosity.NORMAL);
      showErrorTraces = true;
    }
  }

  @Preference(name = "quiet")
  @Option(name = "q", longName = "quiet", optionalArg = true)
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
  @Option(name = "C", longName = "color", optionalArg = true)
  protected void enableAnsiColors(final Boolean flag) {
    Ansi.setEnabled(flag);
  }

  // TODO: Add helpers to control terminal

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

  @VisibleForTesting
  protected StreamSet createStreamSet() {
    return StreamSet.SYSTEM_FD;
  }

  public void boot(String... args) throws Exception {
    checkNotNull(args);

    args = Arguments.clean(args);
    log.debug("Booting w/args: {}", Arrays.asList(args));

    // Register default handler for uncaught exceptions
    Thread.setDefaultUncaughtExceptionHandler((thread, cause) -> log.warn("Unhandled exception occurred on thread: " + thread, cause));

    io = new AnsiIO(createStreamSet(), true);
    vars = new VariablesSupport();

    // Setup environment defaults
    setConsoleLogLevel(Level.INFO);

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
      HelpPrinter printer = new HelpPrinter(clp, io.getTerminal());
      printer.printUsage(io.out, getBranding().getProgramName());
      exit(ExitNotification.SUCCESS_CODE);
    }

    if (version) {
      io.out.format("%s %s", getBranding().getDisplayName(), getBranding().getVersion()).println();
      exit(ExitNotification.SUCCESS_CODE);
    }

    StreamJack.maybeInstall(io.streams);

    // Setup a reference for our exit code so our callback thread can tell if we've shutdown normally or not
    final AtomicReference<Integer> codeRef = new AtomicReference<Integer>();
    Object result = null;

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (codeRef.get() == null) {
        // Give the user a warning when the JVM shutdown abnormally, normal shutdown
        // will set an exit code through the proper channels

        io.err.println();
        io.err.println(messages.getMessage("warning.abnormalShutdown"));
      }

      io.flush();
    }));

    try {
      vars.set(VariableNames.SHELL_ERRORS, showErrorTraces);

      Shell shell = createShell();
      ShellHolder.set(shell);

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
      result = vars.get(VariableNames.LAST_RESULT);
    }

    int code = ExitCodeDecoder.decode(result);
    codeRef.set(code);
    exit(code);
  }

  protected abstract Branding createBranding();

  protected abstract Shell createShell() throws Exception;
}
