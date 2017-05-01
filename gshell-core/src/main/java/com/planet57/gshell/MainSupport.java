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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.planet57.gossip.Level;
import com.planet57.gossip.Log;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.internal.BeanContainer;
import com.planet57.gshell.internal.ExitCodeDecoder;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellBuilder;
import com.planet57.gshell.util.NameValue;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.util.io.StreamSet;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.util.pref.Preferences;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.apache.felix.gogo.runtime.threadio.ThreadIOImpl;
import org.apache.felix.service.threadio.ThreadIO;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sonatype.goodies.common.Throwables2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  private final ThreadIOImpl threadIO = new ThreadIOImpl();

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
    log.debug("Logging threshold: {}", level);
    setConsoleLoggingThreshold(level);
    System.setProperty("shell.logging.file.threshold", level.name());
    System.setProperty("shell.logging.root-level", level.name());
  }

  @Preference(name = "debug")
  @Option(name = "d", longName = "debug", optionalArg = true)
  private void setDebug(final boolean flag) {
    log.debug("Debug: {}", flag);
    if (flag) {
      setLoggingThreshold(Level.DEBUG);
      showErrorTraces = true;
    }
  }

  @Preference(name = "trace")
  @Option(name = "X", longName = "trace", optionalArg = true)
  private void setTrace(final boolean flag) {
    log.debug("Trace: {}", flag);
    if (flag) {
      setLoggingThreshold(Level.TRACE);
      showErrorTraces = true;
    }
  }

  @Nullable
  @Option(name = "c", longName = "command")
  private String command;

  private final Variables variables = new VariablesSupport();

  @Option(name = "D", longName = "define")
  private void setVariable(final String input) {
    log.debug("Set variable: {}", input);
    NameValue nv = NameValue.parse(input);
    variables.set(nv.name, nv.value);
  }

  @Option(name = "P", longName = "property")
  private void setSystemProperty(final String input) {
    log.debug("Set system-property: {}", input);
    NameValue nv = NameValue.parse(input);
    System.setProperty(nv.name, nv.value);
  }

  @Preference(name = "color")
  @Option(name = "C", longName = "color", optionalArg = true)
  private void enableAnsiColors(final Boolean flag) {
    log.debug("Color: {}", flag);
    Ansi.setEnabled(flag);
  }

  @Argument
  @Nullable
  private List<String> appArgs = null;

  /**
   * Create a the {@link Branding} instance.
   *
   * Branding is needed very early to allow customization of command-line processing.
   */
  protected Branding createBranding() {
    return new BrandingSupport();
  }

  public void boot(final String... args) throws Exception {
    checkNotNull(args);

    if (log.isDebugEnabled()) {
      log.debug("Booting w/args: {}", Arrays.asList(args));
    }

    // Register default handler for uncaught exceptions
    Thread.setDefaultUncaughtExceptionHandler((thread, cause) -> log.warn("Unhandled exception occurred on thread: {}", thread, cause));

    // Setup environment defaults
    setConsoleLoggingThreshold(Level.INFO);

    // Prepare branding
    Branding branding = createBranding();

    // Process preferences
    PreferenceProcessor pp = new PreferenceProcessor();
    pp.setBasePath(branding.getPreferencesBasePath());
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
      System.err.println(Throwables2.explain(e));
      if (showErrorTraces) {
        e.printStackTrace(System.err);
      }
      exit(2);
    }

    // adapt JUL
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    Terminal terminal = createTerminal(branding);
    IO io = new IO(createStreamSet(terminal), terminal);

    if (help) {
      HelpPrinter printer = new HelpPrinter(clp, terminal.getWidth());
      printer.printUsage(io.out, branding.getProgramName());
      io.flush();
      exit(0);
    }

    if (version) {
      io.out.format("%s %s%n", branding.getDisplayName(), branding.getVersion());
      io.flush();
      exit(0);
    }

    // install thread-IO handler
    threadIO.start();
    threadIO.setStreams(io.streams.in, io.streams.out, io.streams.err);

    Object result = null;
    try {
      variables.set(VariableNames.SHELL_ERRORS, showErrorTraces);

      Shell shell = createShell(io, variables, branding);
      shell.start();
      try {
        if (command != null) {
          result = shell.execute(command);
        }
        else if (appArgs != null) {
          result = shell.execute(String.join(" ", appArgs));
        }
        else {
          shell.run();
        }
      }
      finally {
        shell.stop();
      }
    }
    finally {
      io.flush();
      threadIO.stop();
      terminal.close();
    }

    if (result == null) {
      result = variables.get(VariableNames.LAST_RESULT);
    }

    exit(ExitCodeDecoder.decode(result));
  }

  //
  // Shell creation
  //

  private final BeanContainer container = new BeanContainer();

  /**
   * Create a new {@link Shell}.
   */
  @VisibleForTesting
  protected Shell createShell(final IO io, final Variables variables, final Branding branding) throws Exception {
    List<Module> modules = new ArrayList<>();

    URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
    modules.add(new SpaceModule(space, BeanScanning.INDEX));

    modules.add(binder -> {
      binder.bind(BeanContainer.class).toInstance(container);
      binder.bind(ThreadIO.class).toInstance(threadIO);
    });

    configure(modules);

    Injector injector = Guice.createInjector(new WireModule(modules));
    container.add(injector, 0);

    return injector.getInstance(ShellBuilder.class)
      .branding(branding)
      .io(io)
      .variables(variables)
      .build();
  }

  /**
   * Allow sub-class to customize container.
   */
  protected void configure(@Nonnull final List<Module> modules) {
    // empty
  }

  //
  // Helpers
  //

  /**
   * Create the {@link Terminal}.
   */
  @VisibleForTesting
  protected Terminal createTerminal(final Branding branding) throws Exception {
    return TerminalBuilder.builder()
      .name(branding.getProgramName())
      .system(true)
      .nativeSignals(true)
      .signalHandler(Terminal.SignalHandler.SIG_IGN) // ignore signals by default
      .build();
  }

  /**
   * Create the {@link StreamSet} used to register.
   */
  @VisibleForTesting
  protected StreamSet createStreamSet(final Terminal terminal) {
    InputStream in = new FilterInputStream(terminal.input())
    {
      @Override
      public void close() throws IOException {
        // ignore
      }
    };
    PrintStream out = new PrintStream(terminal.output())
    {
      @Override
      public void close() {
        // ignore
      }
    };
    return new StreamSet(in, out);
  }

  /**
   * Allow control of exit behavior.
   */
  @VisibleForTesting
  protected void exit(final int code) {
    log.debug("Existing with code: {}", code);
    System.exit(code);
  }
}
