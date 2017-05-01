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
import java.util.Arrays;
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
import org.apache.felix.gogo.jline.Builtin;
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
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.Terminal.SignalHandler;
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

  private final CommandProcessorImpl commandProcessor;

  private final Completer completer;

  private final ShellPrompt prompt;

  private final ShellErrorHandler errorHandler;

  private final History history;

  private final ShellScriptLoader scriptLoader;

  private final Branding branding;

  private final IO io;

  private final Variables variables;

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
    checkNotNull(commandRegistrar);
    this.commandProcessor = checkNotNull(commandProcessor);
    this.branding = checkNotNull(branding);
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.completer = checkNotNull(completer);

    this.prompt = new ShellPrompt();
    this.errorHandler = new ShellErrorHandler();
    this.history = new DefaultHistory();
    this.scriptLoader = new ShellScriptLoader();

    lifecycles.add(events, commandRegistrar);
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
    // HACK: install some gogo-jline commands
    // register(commandProcessor, new Builtin(), "jobs", "bg", "fg");

    CommandSessionImpl session = commandProcessor.createSession(io.streams.in, io.streams.out, io.streams.err);
    session.put(CommandActionFunction.SHELL_VAR, this);
    session.put(CommandActionFunction.TERMINAL_VAR, io.terminal);

    // FIXME: copy variables to session; can't presently provide the underlying map; this breaks dynamic variable setting
    session.getVariables().putAll(variables.asMap());

    currentSession = session;

    scriptLoader.loadProfileScripts(this);
  }

  // HACK: install gogo functions
//  private static void register(final CommandProcessorImpl commandProcessor, final Object target, final String... functions) {
//    Arrays.stream(functions).forEach(function -> {
//      commandProcessor.addCommand("gogo", target, function);
//    });
//  }

  private void doStop() throws Exception {
    if (currentSession != null) {
      currentSession.close();
      currentSession = null;
    }

    lineReader = null;

    lifecycles.stop();
  }

  @Override
  public Branding getBranding() {
    return branding;
  }

  @Override
  public Terminal getTerminal() {
    return io.terminal;
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

    // handle CTRL-C
    SignalHandler interruptHandler = terminal.handle(Signal.INT, s -> {
      Job current = session.foregroundJob();
      if (current != null) {
        log.debug("Interrupting task: {}", current);
        current.interrupt();
      }
    });

    // handle CTRL-Z
    SignalHandler suspendHandler = terminal.handle(Signal.TSTP, s -> {
      Job current = session.foregroundJob();
      if (current != null) {
        log.debug("Suspending task: {}", current);
        current.suspend();
      }
    });

    // TODO: for proper CTRL-Z we need fg, bg and jobs commands; see gogo.jline.Builtin

    log.trace("Running");
    boolean running = true;
    try {
      while (running) {
        try {
          String line = lineReader.readLine(prompt.prompt(this), prompt.rprompt(this), null, null);
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
          running = errorHandler.handleError(io, t, variables.require(VariableNames.SHELL_ERRORS, Boolean.class, true));
        }
      }
    }
    finally {
      terminal.handle(Signal.INT, interruptHandler);
      terminal.handle(Signal.TSTP, suspendHandler);
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
