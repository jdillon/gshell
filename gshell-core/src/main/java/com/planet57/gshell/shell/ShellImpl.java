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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.util.jline.TerminalHolder;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.sonatype.goodies.lifecycle.LifecycleSupport;
import org.sonatype.goodies.lifecycle.Lifecycles;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link Shell} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
public class ShellImpl
  extends LifecycleSupport
  implements Shell
{
  private final EventManager events;

  private final Branding branding;

  private final CommandExecutor executor;

  private final CommandRegistrar commandRegistrar;

  private IO io;

  private final Variables variables;

  private final History history;

  private Completer completer;

  private ConsolePrompt prompt;

  private ConsoleErrorHandler errorHandler;

  private boolean opened;

  // TODO: Maybe these should be set in variables?  More supportable than adding new methods for little features like this.

  private boolean loadProfileScripts = true;

  private boolean loadInteractiveScripts = true;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final CommandExecutor executor,
                   final Branding branding,
                   @Named("main") final IO io,
                   @Named("main") final Variables variables,
                   @Named("shell") final Completer completer)
      throws IOException
  {
    this.events = checkNotNull(events);
    this.executor = checkNotNull(executor);
    this.commandRegistrar = checkNotNull(commandRegistrar);
    this.branding = checkNotNull(branding);
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.completer = checkNotNull(completer);

    // HACK: adapt variables for events
    if (variables instanceof VariablesSupport) {
      ((VariablesSupport) variables).setEventManager(events);
    }

    // FIXME: looks like we have to set jline LineReader.HISTORY_FILE variable to control this location
    this.history = new DefaultHistory();
  }

  @Override
  protected void doStart() throws Exception {
    Lifecycles.start(events);
    Lifecycles.start(commandRegistrar);
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
  public void setPrompt(@Nullable final ConsolePrompt prompt) {
    this.prompt = prompt;
    log.debug("Prompt: {}", prompt);
  }

  @Inject
  public void setErrorHandler(@Nullable final ConsoleErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    log.debug("Error handler: {}", errorHandler);
  }

  public void setCompleter(@Nullable final Completer completer) {
    log.debug("Completer: {}", completer);
    this.completer = completer;
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
    checkNotNull(args);
    ensureOpened();

    log.debug("Starting interactive console; args: {}", Arrays.asList(args));

    final Terminal previousTerminal = TerminalHolder.set(io.getTerminal());
    final Shell lastShell = ShellHolder.set(this);

    try {
      loadInteractiveScripts();

      // Setup 2 final refs to allow our executor to pass stuff back to us
      final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<>();

      Callable<ConsoleTask> taskFactory = () -> new ConsoleTask() {
        @Override
        public boolean doExecute(final String input) throws Exception {
          try {
            // result is saved to LAST_RESULT via the CommandExecutor
            ShellImpl.this.execute(input);
          } catch (ExitNotification n) {
            exitNotifHolder.set(n);
            return false;
          }

          return true;
        }
      };

      Console console = new Console(io, taskFactory, history, new LoggingCompleter(completer));

      if (prompt != null) {
        console.setPrompt(prompt);
      }

      if (errorHandler != null) {
        console.setErrorHandler(errorHandler);
      }

      if (!io.isQuiet()) {
        renderWelcomeMessage(io);
      }

      // Check if there are args, and run them and then enter interactive
      if (args.length != 0) {
        execute(args);
      }

      console.run();

      if (!io.isQuiet()) {
        renderGoodbyeMessage(io);
      }

      // If any exit notification occurred while running, then puke it up
      ExitNotification n = exitNotifHolder.get();
      if (n != null) {
        throw n;
      }
    }
    finally {
      TerminalHolder.set(previousTerminal);
      ShellHolder.set(lastShell);
    }
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
    checkNotNull(file);
    log.debug("Loading script: {}", file);

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        execute(line);
      }
    }
  }

  protected void loadUserScript(final String fileName) throws Exception {
    checkNotNull(fileName);
    File file = new File(branding.getUserContextDir(), fileName);
    if (file.exists()) {
      loadScript(file);
    }
    else {
      log.trace("User script is not present: {}", file);
    }
  }

  protected void loadSharedScript(final String fileName) throws Exception {
    checkNotNull(fileName);
    File file = new File(branding.getShellContextDir(), fileName);
    if (file.exists()) {
      loadScript(file);
    }
    else {
      log.trace("Shared script is not present: {}", file);
    }
  }
}
