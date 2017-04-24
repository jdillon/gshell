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
import com.planet57.gshell.execute.ExitNotification;
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
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

  private final IO io;

  private final Variables variables;

  private final History history;

  private final Completer completer;

  private final ConsolePrompt prompt;

  private final ConsoleErrorHandler errorHandler;

  private final ShellScriptLoader scriptLoader;

  private volatile boolean opened;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final CommandExecutor executor,
                   final Branding branding,
                   @Named("main") final IO io,
                   @Named("main") final Variables variables,
                   @Named("shell") final Completer completer,
                   final ConsolePrompt prompt,
                   final ConsoleErrorHandler errorHandler)
      throws IOException
  {
    this.events = checkNotNull(events);
    this.executor = checkNotNull(executor);
    this.commandRegistrar = checkNotNull(commandRegistrar);
    this.branding = checkNotNull(branding);
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.completer = checkNotNull(completer);
    this.prompt = checkNotNull(prompt);
    this.errorHandler = checkNotNull(errorHandler);

    // FIXME: looks like we have to set jline LineReader.HISTORY_FILE variable to control this location
    this.history = new DefaultHistory();

    // FIXME: for now leave this as default non-configurable
    this.scriptLoader = new ShellScriptLoader();
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

  @Override
  public synchronized boolean isOpened() {
    return opened;
  }

  @Override
  public synchronized void close() {
    // FIXME: this is pretty silly
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
    scriptLoader.loadProfileScripts(this);
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

    final Shell lastShell = ShellHolder.set(this);

    try {
      scriptLoader.loadInteractiveScripts(this);

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

      LineReader lineReader = LineReaderBuilder.builder()
        .terminal(io.getTerminal())
        .history(history)
        .completer(new LoggingCompleter(completer))
        .build();

      Console console = new Console(lineReader, prompt, taskFactory, errorHandler);

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
      ShellHolder.set(lastShell);
    }
  }

  /**
   * Helper to optionally render a welcome or goodbye message.
   */
  private static void renderMessage(final IO io, @Nullable final String msg) {
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
}
