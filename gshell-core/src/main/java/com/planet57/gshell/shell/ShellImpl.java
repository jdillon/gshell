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
package com.planet57.gshell.shell;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.console.Console;
import com.planet57.gshell.console.ConsoleErrorHandler;
import com.planet57.gshell.console.ConsolePrompt;
import com.planet57.gshell.console.ConsoleTask;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.execute.CommandExecutor;
import com.planet57.gshell.notification.ExitNotification;
import com.planet57.gshell.util.Arguments;
import com.planet57.gshell.util.io.Closer;
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesImpl;
import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link Shell} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
public class ShellImpl
    implements Shell
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final EventManager events;

  private final Branding branding;

  private final CommandExecutor executor;

  private final CommandRegistrar commandRegistrar;

  private IO io;

  private final Variables variables;

  private final ShellHistory history;

  private List<Completer> completers;

  private ConsolePrompt prompt;

  private ConsoleErrorHandler errorHandler;

  private boolean opened;

  //
  // TODO: Maybe these should be set in variables?  More supportable than adding new methods for little features like this.
  //

  private boolean loadProfileScripts = true;

  private boolean loadInteractiveScripts = true;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final CommandExecutor executor,
                   final Branding branding,
                   @Nullable @Named("main") final IO io,
                   @Nullable @Named("main") final Variables variables)
      throws IOException
  {
    this.events = checkNotNull(events);
    this.executor = checkNotNull(executor);
    this.commandRegistrar = checkNotNull(commandRegistrar);
    this.branding = checkNotNull(branding);

    this.io = io != null ? io : new IO();
    this.variables = variables != null ? variables : new VariablesImpl();

    // HACK: adapt variables for events
    if (variables instanceof VariablesImpl) {
      ((VariablesImpl) variables).setEventManager(events);
    }

    this.history = new ShellHistory(new File(branding.getUserContextDir(), branding.getHistoryFileName()));
  }

  // HACK: primative lifecycle

  public void start() throws Exception {
    events.start();
    commandRegistrar.discoverCommands();
  }

  @Override
  public Branding getBranding() {
    return branding;
  }

  @Override
  public IO getIo() {
    return io;
  }

  @Override
  public Variables getVariables() {
    return variables;
  }

  @Override
  public History getHistory() {
    return history;
  }

  @Inject
  public void setPrompt(final ConsolePrompt prompt) {
    this.prompt = prompt;
  }

  @Inject
  public void setErrorHandler(final ConsoleErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public void setCompleters(final List<Completer> completers) {
    this.completers = completers;
  }

  public void setCompleters(final Completer... completers) {
    if (completers != null) {
      this.completers = Arrays.asList(completers);
    }
  }

  @Inject
  public void installCompleters(final @Named("alias-name") Completer c1, final @Named("commands") Completer c2) {
    checkNotNull(c1);
    checkNotNull(c2);
    setCompleters(new AggregateCompleter(c1, c2));
  }

  public boolean isLoadProfileScripts() {
    return loadProfileScripts;
  }

  public void setLoadProfileScripts(boolean enable) {
    this.loadProfileScripts = enable;
  }

  public boolean isLoadInteractiveScripts() {
    return loadInteractiveScripts;
  }

  public void setLoadInteractiveScripts(boolean enable) {
    this.loadInteractiveScripts = enable;
  }

  public synchronized boolean isOpened() {
    return opened;
  }

  @Override
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

    StreamJack.maybeInstall();

    // Customize the shell
    branding.customize(this);

    opened = true;
    log.debug("Opened");

    // Do this after we are marked as opened
    loadProfileScripts();
  }

  @Override
  public boolean isInteractive() {
    return true;
  }

  // FIXME: History should still be appended if not running inside of a JLineConsole

  @Override
  public Object execute(final CharSequence line) throws Exception {
    ensureOpened();
    return executor.execute(this, String.valueOf(line));
  }

  @Override
  public Object execute(final CharSequence command, final Object[] args) throws Exception {
    ensureOpened();
    return executor.execute(this, String.valueOf(command), args);
  }

  @Override
  public Object execute(final Object... args) throws Exception {
    ensureOpened();
    return executor.execute(this, args);
  }

  @Override
  public void run(final Object... args) throws Exception {
    assert args != null;
    ensureOpened();

    log.debug("Starting interactive console; args: {}", Arguments.toStringArray(args));

    loadInteractiveScripts();

    // Setup 2 final refs to allow our executor to pass stuff back to us
    final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<ExitNotification>();

    Callable<ConsoleTask> taskFactory = new Callable<ConsoleTask>()
    {
      public ConsoleTask call() throws Exception {
        return new ConsoleTask()
        {
          @Override
          public boolean doExecute(final String input) throws Exception {
            try {
              // result is saved to LAST_RESULT via the CommandExecutor
              ShellImpl.this.execute(input);
            }
            catch (ExitNotification n) {
              exitNotifHolder.set(n);
              return false;
            }

            return true;
          }
        };
      }
    };

    IO io = getIo();

    Console console = new Console(io, taskFactory, history, loadBindings());

    if (prompt != null) {
      console.setPrompt(prompt);
    }

    if (errorHandler != null) {
      console.setErrorHandler(errorHandler);
    }

    if (completers != null && !completers.isEmpty()) {
      for (Completer completer : completers) {
        console.addCompleter(completer != null ? completer : NullCompleter.INSTANCE);
      }
    }

    if (!io.isQuiet()) {
      renderWelcomeMessage(io);
    }

    // Check if there are args, and run them and then enter interactive
    if (args.length != 0) {
      execute(args);
    }

    // HACK: We have to replace the IO with the consoles so that children use the piped input
    final IO lastIo = io;
    this.io = console.getIo();

    final Shell lastShell = ShellHolder.set(this);

    try {
      console.run();
    }
    finally {
      this.io = lastIo;
      ShellHolder.set(lastShell);
    }

    if (!io.isQuiet()) {
      renderGoodbyeMessage(io);
    }

    // If any exit notification occurred while running, then puke it up
    ExitNotification n = exitNotifHolder.get();
    if (n != null) {
      throw n;
    }
  }

  private InputStream loadBindings() throws IOException {
    File file = new File(branding.getUserContextDir(), ConsoleReader.JLINE_KEYBINDINGS);

    if (!file.exists() || !file.isFile()) {
      file = new File(branding.getShellContextDir(), ConsoleReader.JLINE_KEYBINDINGS);
      if (!file.exists() || file.isFile()) {
        try {
          String fileName = System.getProperty(ConsoleReader.JLINE_KEYBINDINGS);
          if (fileName != null) {
            file = new File(fileName);
          }
          if (!file.exists() || file.isFile()) {
            file = new File(branding.getUserHomeDir(), ConsoleReader.JLINEBINDINGS_PROPERTIES);
          }
        }
        catch (Exception e) {
          log.warn("Failed to load key-bindings", e);
        }
      }
    }

    InputStream bindings;

    if (file.exists() && file.isFile() && file.canRead()) {
      log.debug("Using bindings from file: {}", file);
      bindings = new BufferedInputStream(new FileInputStream(file));
    }
    else {
      log.trace("Using default bindings");
      bindings = io.getTerminal().getDefaultBindings();
    }

    return bindings;
  }

  private void renderMessage(final IO io, final String msg) {
    assert io != null;
    if (msg != null) {
      io.out.println(msg);
      io.out.flush();
    }
  }

  protected void renderWelcomeMessage(final IO io) {
    renderMessage(io, branding.getWelcomeMessage());
  }

  protected void renderGoodbyeMessage(final IO io) {
    renderMessage(io, branding.getGoodbyeMessage());
  }

  // Script Loader

  protected void loadProfileScripts() throws Exception {
    if (!isLoadProfileScripts()) {
      return;
    }

    String fileName = branding.getProfileScriptName();
    loadSharedScript(fileName);
    loadUserScript(fileName);
  }

  protected void loadInteractiveScripts() throws Exception {
    if (!isLoadInteractiveScripts()) {
      return;
    }

    String fileName = branding.getInteractiveScriptName();
    loadSharedScript(fileName);
    loadUserScript(fileName);
  }

  protected void loadScript(final File file) throws Exception {
    assert file != null;

    log.debug("Loading script: {}", file);

    BufferedReader reader = new BufferedReader(new FileReader(file));
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        execute(line);
      }
    }
    finally {
      Closer.close(reader);
    }
  }

  protected void loadUserScript(final String fileName) throws Exception {
    assert fileName != null;

    File file = new File(branding.getUserContextDir(), fileName);
    if (file.exists()) {
      loadScript(file);
    }
    else {
      log.trace("User script is not present: {}", file);
    }
  }

  protected void loadSharedScript(final String fileName) throws Exception {
    assert fileName != null;

    File file = new File(branding.getShellContextDir(), fileName);
    if (file.exists()) {
      loadScript(file);
    }
    else {
      log.trace("Shared script is not present: {}", file);
    }
  }
}
