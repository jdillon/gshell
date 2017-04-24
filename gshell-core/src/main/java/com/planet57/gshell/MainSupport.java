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
import com.planet57.gshell.command.IO;
import com.planet57.gshell.execute.ExitNotification;
import com.planet57.gshell.internal.ExitCodeDecoder;
import com.planet57.gshell.shell.Shell;
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
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

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
  private final Logger log = Log.getLogger(getClass());

  private final MessageSource messages = new ResourceBundleMessageSource()
    .add(false, getClass())
    .add(MainSupport.class);

  private IO io;

  /**
   * Visible for {@link com.planet57.gshell.guice.GuiceMainSupport}.
   */
  protected IO getIo() {
    return io;
  }

  private Variables vars;

  /**
   * Visible for {@link com.planet57.gshell.guice.GuiceMainSupport}.
   */
  protected Variables getVariables() {
    return vars;
  }

  private Branding branding;

  @Option(name = "h", longName = "help", override = true)
  private boolean help;

  @Option(name = "V", longName = "version", override = true)
  private boolean version;

  @Preference
  @Option(name = "e", longName = "errors", optionalArg = true)
  private boolean showErrorTraces = false;

  /**
   * Adjust the threshold of the {@code console} appender.
   */
  private void setConsoleLoggingThreshold(final Level level) {
    System.setProperty("shell.logging.console.threshold", level.name());
  }

  /**
   * Adjust the threshold of all logging.
   */
  private void setLoggingThreshold(final Level level) {
    setConsoleLoggingThreshold(level);
    System.setProperty("shell.logging.file.threshold", level.name());
    System.setProperty("shell.logging.root-level", level.name());
  }

  @Preference(name = "debug")
  @Option(name = "d", longName = "debug", optionalArg = true)
  private void setDebug(final boolean flag) {
    if (flag) {
      setLoggingThreshold(Level.DEBUG);
      showErrorTraces = true;
    }
  }

  @Preference(name = "trace")
  @Option(name = "X", longName = "trace", optionalArg = true)
  private void setTrace(final boolean flag) {
    if (flag) {
      setLoggingThreshold(Level.TRACE);
      showErrorTraces = true;
    }
  }

  @Option(name = "c", longName = "command")
  private String command;

  @Option(name = "D", longName = "define")
  private void setVariable(final String input) {
    NameValue nv = NameValue.parse(input);
    vars.set(nv.name, nv.value);
  }

  @Option(name = "P", longName = "property")
  private void setSystemProperty(final String input) {
    NameValue nv = NameValue.parse(input);
    System.setProperty(nv.name, nv.value);
  }

  @Preference(name = "color")
  @Option(name = "C", longName = "color", optionalArg = true)
  private void enableAnsiColors(final Boolean flag) {
    Ansi.setEnabled(flag);
  }

  @Argument()
  private List<String> appArgs = null;

  /**
   * Allow control of exit behavior.
   */
  @VisibleForTesting
  protected void exit(final int code) {
    io.flush();
    System.exit(code);
  }

  /**
   * Branding is lazily-loaded.
   *
   * @see #createBranding()
   */
  protected Branding getBranding() {
    if (branding == null) {
      branding = createBranding();
    }
    return branding;
  }

  /**
   * Create a the {@link Branding} instance.
   *
   * @see #getBranding()
   */
  protected abstract Branding createBranding();

  /**
   * Create the {@link StreamSet} used to register.
   */
  @VisibleForTesting
  protected StreamSet createStreamSet() {
    return StreamSet.SYSTEM_FD;
  }

  /**
   * Create the {@link Shell} instance.
   */
  protected abstract Shell createShell() throws Exception;

  public void boot(String... args) throws Exception {
    checkNotNull(args);

    args = Arguments.clean(args);
    log.debug("Booting w/args: {}", Arrays.asList(args));

    // Register default handler for uncaught exceptions
    Thread.setDefaultUncaughtExceptionHandler((thread, cause) -> log.warn("Unhandled exception occurred on thread: " + thread, cause));

    Terminal terminal = TerminalBuilder.builder()
      .build();

    io = new IO(createStreamSet(), terminal);
    vars = new VariablesSupport();

    // Setup environment defaults
    setConsoleLoggingThreshold(Level.INFO);

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
      HelpPrinter printer = new HelpPrinter(clp, io.terminal);
      printer.printUsage(io.out, getBranding().getProgramName());
      exit(ExitNotification.SUCCESS_CODE);
    }

    if (version) {
      io.out.format("%s %s", getBranding().getDisplayName(), getBranding().getVersion()).println();
      exit(ExitNotification.SUCCESS_CODE);
    }

    // adapt JUL and force slf4j backend to initialize
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    // hijack streams
    StreamJack.maybeInstall(io.streams);

    // Setup a reference for our exit code so our callback thread can tell if we've shutdown normally or not
    final AtomicReference<Integer> codeRef = new AtomicReference<>();
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
}
