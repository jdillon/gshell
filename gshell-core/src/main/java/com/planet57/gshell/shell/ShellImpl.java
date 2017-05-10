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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.command.CommandAction.ExitNotification;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.help.HelpPageManager;
import com.planet57.gshell.internal.CommandActionFunction;
import com.planet57.gshell.internal.CommandProcessorImpl;
import com.planet57.gshell.internal.VariablesProvider;
import com.planet57.gshell.util.jline.LoggingCompleter;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.apache.felix.gogo.jline.Builtin;
import org.apache.felix.gogo.jline.Expander;
import org.apache.felix.gogo.jline.Highlighter;
import org.apache.felix.gogo.jline.ParsedLineImpl;
import org.apache.felix.gogo.jline.Parser;
import org.apache.felix.gogo.jline.Posix;
import org.apache.felix.gogo.jline.Procedural;
import org.apache.felix.gogo.runtime.Closure;
import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Job;
import org.fusesource.jansi.AnsiRenderer;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.Terminal.SignalHandler;
import org.jline.utils.InfoCmp;
import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.goodies.lifecycle.LifecycleManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.planet57.gshell.variables.VariableNames.SHELL_PROMPT;
import static com.planet57.gshell.variables.VariableNames.SHELL_RPROMPT;

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

  private final ShellErrorHandler errorHandler;

  private final History history;

  private final ShellScriptLoader scriptLoader;

  private IO io;

  private Variables variables;

  private Branding branding;

  private CommandSessionImpl currentSession;

  private LineReader lineReader;

  @Inject
  public ShellImpl(final EventManager events,
                   final CommandRegistrar commandRegistrar,
                   final HelpPageManager helpPageManager,
                   final CommandProcessorImpl commandProcessor,
                   @Named("shell") final Completer completer)
  {
    checkNotNull(events);
    checkNotNull(commandRegistrar);
    checkNotNull(helpPageManager);

    this.commandProcessor = checkNotNull(commandProcessor);
    this.completer = checkNotNull(completer);

    this.errorHandler = new ShellErrorHandler();
    this.history = new DefaultHistory();
    this.scriptLoader = new ShellScriptLoader();

    lifecycles.add(events, commandRegistrar, helpPageManager);
  }

  /**
   * Initialize runtime state; must be called before {@link #start()}.
   */
  public void init(final IO io, final Variables variables, final Branding branding) {
    this.io = checkNotNull(io);
    this.variables = checkNotNull(variables);
    this.branding = checkNotNull(branding);

    // HACK: more variables shenanigans
    VariablesProvider.set(variables);
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
    checkState(io != null);
    checkState(variables != null);
    checkState(branding != null);

    lifecycles.start();

    // apply any branding customization
    branding.customize(this);
  }

  private void doStarted() throws Exception {
    // HACK: register some gogo functions
    commandProcessor.registerFunction(new Builtin(),
      "jobs", "bg", "fg", "new", "type", "tac"
    );
    commandProcessor.registerFunction(new Posix(commandProcessor),
      "cat", "wc", "grep", "head", "tail", "sort", "watch"
    );
    commandProcessor.registerFunction(new Procedural(),
      "each", "if", "not", "throw", "try", "until", "while", "break", "continue"
    );

    CommandSessionImpl session = commandProcessor.createSession(io.streams.in, io.streams.out, io.streams.err);
    session.put(CommandActionFunction.SHELL_VAR, this);
    session.put(CommandActionFunction.TERMINAL_VAR, io.terminal);

    // FIXME: copy variables to session; can't presently provide the underlying map; this breaks dynamic variable setting
    session.getVariables().putAll(variables.asMap());

    currentSession = session;

    scriptLoader.loadProfileScripts(this);
  }

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

    CommandSessionImpl session = currentSession;

    Object result;
    try {
      result = session.execute(line);
      setLastResult(session, result);
    }
    catch (Throwable failure) {
      Throwable cause = failure;

      // gogo encodes Error as ExecutionException; decode here
      if (cause instanceof ExecutionException) {
        cause = cause.getCause();
      }

      log.trace("Failure", cause);
      setLastResult(session, cause);
      Throwables.propagateIfPossible(cause, Exception.class, Error.class);
      throw failure;
    }
    finally {
      // HACK: copy session variables back to shell's variables
      variables.asMap().clear();
      variables.asMap().putAll(session.getVariables());
      VariablesProvider.set(variables);
    }

    return result;
  }

  @Override
  public void run() throws Exception {
    ensureStarted();

    log.debug("Starting interactive console");

    final CommandSessionImpl session = currentSession;

    scriptLoader.loadInteractiveScripts(this);

    final Terminal terminal = io.terminal;

    // HACK: testing adjustment to highlighter colors; pending letting users configure this via profile/rc
    int maxColors = terminal.getNumericCapability(InfoCmp.Capability.max_colors);
    if (maxColors >= 256) {
      session.put("HIGHLIGHTER_COLORS","rs=35:st=32:nu=32:co=32:va=36:vn=36:fu=1;38;5;69:bf=1;38;5;197:re=90");
    }
    else {
      session.put("HIGHLIGHTER_COLORS","rs=35:st=32:nu=32:co=32:va=36:vn=36:fu=94:bf=91:re=90");
    }

    File historyFile = new File(branding.getUserContextDir(), branding.getHistoryFileName());

    lineReader = LineReaderBuilder.builder()
      .appName(branding.getProgramName())
      .terminal(terminal)
      .parser(new Parser()) // install gogo-jline program accessible parser impl
      .expander(new Expander(session))
      .completer(new LoggingCompleter(completer))
      .highlighter(new Highlighter(session))
      .history(history)
      .variables(session.getVariables())
      .variable(LineReader.HISTORY_FILE, historyFile)
      .build();

    // automatically freshen line; this handles redrawing the line on CTRL-C
    lineReader.setOpt(LineReader.Option.AUTO_FRESH_LINE);

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

    log.trace("Running");
    boolean running = true;
    try {
      while (running) {
        String line = lineReader.readLine(prompt(session), rprompt(session), null, null);
        if (log.isTraceEnabled()) {
          traceLine(line);
        }

        ParsedLineImpl parsedLine = (ParsedLineImpl) lineReader.getParsedLine();
        if (parsedLine == null) {
          throw new EndOfFileException();
        }

        try {
          execute(parsedLine.program());
        }
        catch (ExitNotification notification) {
          log.trace("Exit requested", notification);
          running = false;
        }
        catch (Throwable failure) {
          boolean verbose = variables.require(VariableNames.SHELL_ERRORS, Boolean.class, true);
          running = errorHandler.handleError(io.err, failure, verbose);
        }

        waitForJobCompletion(session);

        // TODO: investigate jline.Shell handling of UserInterruptException and EndOfFileException here
      }
    }
    finally {
      terminal.handle(Signal.INT, interruptHandler);
      terminal.handle(Signal.TSTP, suspendHandler);
    }
    log.trace("Stopped");

    renderMessage(io, branding.getGoodbyeMessage());
  }

  /**
   * Wait for current job, if any, to complete.
   */
  @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
  private void waitForJobCompletion(final CommandSessionImpl session) throws InterruptedException {
    while (true) {
      Job job = session.foregroundJob();
      if (job == null) break;

      log.debug("Waiting for job completion: {}", job);
      synchronized (job) {
        if (job.status() == Job.Status.Foreground) {
          job.wait();
        }
      }
    }
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

  //
  // Prompts
  //

  @Nullable
  private String expand(final CommandSessionImpl session, @Nullable final Object value) {
    if (value != null) {
      try {
        Object result = org.apache.felix.gogo.runtime.Expander.expand(value.toString(), new Closure(session, null, null));
        if (result != null) {
          return result.toString();
        }
      }
      catch (Exception e) {
        log.warn("Failed to expand: {}", value, e);
      }
    }
    return null;
  }

  private String prompt(final CommandSessionImpl session) {
    Object value = session.get(SHELL_PROMPT);
    if (value == null) {
      value = branding.getPrompt();
    }

    String prompt = expand(session, value);

    // fail-safe prompt
    if (prompt == null) {
      prompt = String.format("%s> ", branding.getProgramName());
    }

    // FIXME: may need to adjust ansi-renderer syntax or pre-render before expanding to avoid needing escapes
    if (AnsiRenderer.test(prompt)) {
      prompt = AnsiRenderer.render(prompt);
    }

    return prompt;
  }

  @Nullable
  private String rprompt(final CommandSessionImpl session) {
    Object value = session.get(SHELL_RPROMPT);
    if (value == null) {
      value = branding.getRightPrompt();
    }

    String prompt = expand(session, value);

    // FIXME: may need to adjust ansi-renderer syntax or pre-render before expanding to avoid needing escapes
    if (AnsiRenderer.test(prompt)) {
      prompt = AnsiRenderer.render(prompt);
    }

    return prompt;
  }
}
