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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Strings;
import com.google.inject.assistedinject.Assisted;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.CommandAction.ExitNotification;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.internal.CommandActionFunction;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.apache.felix.gogo.jline.Expander;
import org.apache.felix.gogo.jline.ParsedLineImpl;
import org.apache.felix.gogo.jline.Parser;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Job;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
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

  private final CommandProcessorImpl commandProcessor;

  private final IO io;

  private final Variables variables;

  private final History history;

  private final Completer completer;

  private final ShellPrompt prompt;

  private final ShellErrorHandler errorHandler;

  private final ShellScriptLoader scriptLoader;

  private CommandSessionImpl currentSession;

  private LineReader lineReader;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final CommandProcessorImpl commandProcessor,
                   @Named("shell") final Completer completer,
                   @Assisted final Branding branding,
                   @Assisted final IO io,
                   @Assisted final Variables variables)
  {
    checkNotNull(events);
    this.commandProcessor = checkNotNull(commandProcessor);
    checkNotNull(commandRegistrar);
    this.branding = checkNotNull(branding);
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.completer = checkNotNull(completer);

    this.prompt = new ShellPrompt(variables, branding);
    this.errorHandler = new ShellErrorHandler(variables);

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
    CommandSessionImpl session = commandProcessor.createSession(io.streams.in, io.streams.out, io.streams.err);
    session.put(CommandActionFunction.SHELL_VAR, this);

    // FIXME: copy variables to session; can't presently provide the underlying map; this breaks dynamic variable setting
    session.getVariables().putAll(variables.asMap());

    currentSession = session;

    scriptLoader.loadProfileScripts(this);
  }

  private void doStop() throws Exception {
    lifecycles.stop();
    if (currentSession != null) {
      currentSession.close();
      currentSession = null;
    }
    lineReader = null;
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

    // FIXME: this is likely not correct in terms of using gogo parser

    CommandSessionImpl session = currentSession;
    return session.execute(line);
  }

  @Override
  public void run() throws Exception {
    ensureStarted();

    log.debug("Starting interactive console");

    CommandSessionImpl session = currentSession;

    scriptLoader.loadInteractiveScripts(this);

    File historyFile = new File(branding.getUserContextDir(), branding.getHistoryFileName());

    Terminal terminal = io.terminal;
    lineReader = LineReaderBuilder.builder()
      .appName(branding.getProgramName())
      .terminal(terminal)
      .parser(new Parser()) // install gogo-jline program accessible parser impl
      .expander(new Expander(session))
      .completer(new LoggingCompleter(completer))
      .history(history)
      .variables(session.getVariables())
      .variable(LineReader.HISTORY_FILE, historyFile)
      .build();

    renderMessage(io, branding.getWelcomeMessage());

    // prepare handling for CTRL-C
    Terminal.SignalHandler intHandler = terminal.handle(Terminal.Signal.INT, s -> {
      Job current = session.foregroundJob();
      if (current != null) {
        log.debug("Interrupting task");
        current.interrupt();
      }
    });

    log.trace("Running");
    boolean running = true;
    try {
      while (running) {
        try {
          String line = lineReader.readLine(prompt.prompt(), prompt.rprompt(), null, null);
          if (log.isTraceEnabled()) {
            traceLine(line);
          }

          ParsedLine parsedLine = lineReader.getParsedLine();
          if (parsedLine == null) {
            throw new EndOfFileException();
          }

          Object result = session.execute(((ParsedLineImpl) parsedLine).program());
          setLastResult(session, result);

          running = !(result instanceof ExitNotification);
        }
        catch (Throwable t) {
          log.trace("Work failed", t);
          setLastResult(session, t);
          running = errorHandler.handleError(io, t);
        }
      }
    }
    finally {
      terminal.handle(Terminal.Signal.INT, intHandler);
    }
    log.trace("Stopped");

    renderMessage(io, branding.getGoodbyeMessage());
  }

  private void traceLine(final String line) {
    if (line.length() == 0 || !log.isTraceEnabled()) {
      return;
    }

    StringBuilder hex = new StringBuilder();
    StringBuilder idx = new StringBuilder();

    line.chars().forEach(ch -> {
      hex.append('x').append(Integer.toHexString(ch)).append(' ');
      idx.append(' ').append((char) ch).append("  ");
    });

    log.trace("Read line: {}\n{}\n{}", line, hex, idx);
  }

  private static void setLastResult(final CommandSession session, final Object result) {
    Shell shell = (Shell) session.get(CommandActionFunction.SHELL_VAR);
    shell.getVariables().set(VariableNames.LAST_RESULT, result);
    session.put(VariableNames.LAST_RESULT, result);
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
}
