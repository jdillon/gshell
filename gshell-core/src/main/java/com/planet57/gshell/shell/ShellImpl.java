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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Strings;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.console.Console;
import com.planet57.gshell.console.ConsoleErrorHandler;
import com.planet57.gshell.console.ConsolePrompt;
import com.planet57.gshell.console.ConsoleTask;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.command.execute.CommandExecutor;
import com.planet57.gshell.command.ExitNotification;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.goodies.lifecycle.LifecycleManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Default {@link Shell} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
public class ShellImpl
  extends ComponentSupport
  implements Shell
{
  private final LifecycleManager lifecycles = new LifecycleManager();

  private final Branding branding;

  private final CommandExecutor executor;

  private final IO io;

  private final Variables variables;

  private final History history;

  private final Completer completer;

  private final ConsolePrompt prompt;

  private final ConsoleErrorHandler errorHandler;

  private final ShellScriptLoader scriptLoader;

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
    checkNotNull(events);
    this.executor = checkNotNull(executor);
    checkNotNull(commandRegistrar);
    this.branding = checkNotNull(branding);
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.completer = checkNotNull(completer);
    this.prompt = checkNotNull(prompt);
    this.errorHandler = checkNotNull(errorHandler);

    lifecycles.add(events, commandRegistrar);

    // HACK: exposed here as some commands needs reference to this
    this.history = new DefaultHistory();

    // FIXME: for now leave this as default non-configurable
    this.scriptLoader = new ShellScriptLoader();
  }

  // custom/simplified lifecycle so we can fire do-start and do-started
  private final AtomicBoolean started = new AtomicBoolean(false);

  @Override
  public void start() throws Exception {
    synchronized (started) {
      checkState(!started.get(), "Already started");
      log.debug("Starting");
      doStart();
      started.set(true);
      doStarted();
      log.debug("Started");
    }
  }

  @Override
  public void stop() throws Exception {
    synchronized (started) {
      ensureStarted();
      log.debug("Stopping");
      doStop();
      started.set(false);
      log.debug("Stopped");
    }
  }

  private void ensureStarted() {
    synchronized (started) {
      checkState(started.get(), "Not started");
    }
  }

  private void doStart() throws Exception {
    lifecycles.start();

    // apply any branding customization
    branding.customize(this);
  }

  private void doStarted() throws Exception {
    scriptLoader.loadProfileScripts(this);
  }

  private void doStop() throws Exception {
    lifecycles.stop();
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
  public Object execute(final CharSequence line) throws Exception {
    ensureStarted();
    return executor.execute(this, String.valueOf(line));
  }

  @Override
  public void run() throws Exception {
    ensureStarted();

    log.debug("Starting interactive console");

    scriptLoader.loadInteractiveScripts(this);

    // Setup 2 final refs to allow our executor to pass stuff back to us
    final AtomicReference<ExitNotification> exitNotifHolder = new AtomicReference<>();

    Callable<ConsoleTask> taskFactory = () -> new ConsoleTask() {
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

    File historyFile = new File(branding.getUserContextDir(), branding.getHistoryFileName());

    LineReader lineReader = LineReaderBuilder.builder()
      .terminal(io.terminal)
      .completer(new LoggingCompleter(completer))
      .history(history)
      .variable(LineReader.HISTORY_FILE, historyFile)
      .build();

    Console console = new Console(lineReader, prompt, taskFactory, errorHandler);

    renderWelcomeMessage(io);

    console.run();

    renderGoodbyeMessage(io);

    // If any exit notification occurred while running, then puke it up
    ExitNotification n = exitNotifHolder.get();
    if (n != null) {
      throw n;
    }
  }

  private static void renderMessage(final IO io, @Nullable String message) {
    if (message != null) {
      // HACK: branding does not have easy access to Terminal; so allow a line to be rendered via replacement token
      if (message.contains(BrandingSupport.LINE_TOKEN)) {
        message = message.replace(BrandingSupport.LINE_TOKEN, Strings.repeat("-", io.terminal.getWidth() - 1));
      }
      io.out.println(message);
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
