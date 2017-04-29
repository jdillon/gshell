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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Strings;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.ExitNotification;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.internal.CommandActionFunction;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.variables.Variables;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
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

  // HACK: have to use impl here, as CommandProcessor does not expose enough api

  private final CommandProcessorImpl commandProcessor;

  private final IO io;

  private final Variables variables;

  private final History history;

  private final Completer completer;

  private final ConsolePrompt prompt;

  private final ConsoleErrorHandler errorHandler;

  private final ShellScriptLoader scriptLoader;

  private LineReader lineReader;

  private ConsoleTask currentTask;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final CommandProcessorImpl commandProcessor,
                   final Branding branding,
                   @Named("main") final IO io,
                   @Named("main") final Variables variables,
                   @Named("shell") final Completer completer,
                   final ConsolePrompt prompt,
                   final ConsoleErrorHandler errorHandler)
      throws IOException
  {
    checkNotNull(events);
    this.commandProcessor = checkNotNull(commandProcessor);
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
    checkNotNull(line);

    CommandSessionImpl session = commandProcessor.createSession(io.streams.in, io.streams.out, io.streams.err);
    session.put(CommandActionFunction.SHELL_VAR, this);

    // HACK: stuff all variables into session, this is not ideal however
    session.getVariables().putAll(variables.asMap());

    // FIXME: this doesn't appear to do the trick; because "echo" will resolve to function "echo" :-(
    // disable trace output by default
    session.put("echo", null);

    Object result = session.execute(line);

    session.close();

    return result;
  }

  @Override
  public void run() throws Exception {
    ensureStarted();

    log.debug("Starting interactive console");

    scriptLoader.loadInteractiveScripts(this);

    File historyFile = new File(branding.getUserContextDir(), branding.getHistoryFileName());

    lineReader = LineReaderBuilder.builder()
      .appName("gshell")
      .terminal(io.terminal)
      .completer(new LoggingCompleter(completer))
      .history(history)
      .variable(LineReader.HISTORY_FILE, historyFile)
      .build();

    renderMessage(io, branding.getWelcomeMessage());

    // prepare handling for CTRL-C
    Terminal terminal = lineReader.getTerminal();
    Terminal.SignalHandler intHandler = terminal.handle(Terminal.Signal.INT, s -> {
      interruptTask();
    });

    log.trace("Running");
    boolean running = true;
    try {
      while (running) {
        try {
          running = work();
        }
        catch (Throwable t) {
          log.trace("Work failed", t);
          running = errorHandler.handleError(t);
        }
      }
    }
    finally {
      terminal.handle(Terminal.Signal.INT, intHandler);
    }
    log.trace("Stopped");

    renderMessage(io, branding.getGoodbyeMessage());
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

  //
  // HACK: merged console impl
  //

  private boolean work() throws Exception {
    String line = lineReader.readLine(prompt.prompt());

    // Build the task and execute it
    checkState(currentTask == null);
    currentTask = new ConsoleTask() {
      @Override
      public boolean doExecute(final String input) throws Exception {
        Object result = ShellImpl.this.execute(input);
        // HACK: need to adjust result; pending more gogo investigation

        // stop if exit-notification
        return !(result instanceof ExitNotification);
      }
    };

    try {
      return currentTask.execute(line);
    }
    finally {
      currentTask = null;
    }
  }

  private void interruptTask() {
    ConsoleTask task = currentTask;
    if (task != null) {
      synchronized (task) {
        log.debug("Interrupting task");

        if (task.isStopping()) {
          task.abort();
        }
        else if (task.isRunning()) {
          task.stop();
        }
      }
    }
    else {
      log.debug("No task running to interrupt");
    }
  }
}
