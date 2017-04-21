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
package com.planet57.gshell.console;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.io.StreamSet;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides an abstraction of a console.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Console
    implements Runnable
{
  private static final Logger log = LoggerFactory.getLogger(Console.class);

  private final IO io;

  private final LineReader lineReader;

  private final Callable<ConsoleTask> taskFactory;

  private ConsolePrompt prompt;

  private ConsoleErrorHandler errorHandler;

  private ConsoleTask currentTask;

  private volatile boolean running;

  public Console(final IO io,
                 final Callable<ConsoleTask> taskFactory,
                 final History history,
                 @Nullable final Completer completer)
    throws IOException
  {
    checkNotNull(io);
    this.taskFactory = checkNotNull(taskFactory);

    // TODO: unsure why we are doing this?
    this.io = new IO(
      new StreamSet(io.streams.in, io.streams.out, io.streams.err),
      io.getTerminal(),
      null,
      io.out,
      io.err,
      true
    );

    this.lineReader = LineReaderBuilder.builder()
      .terminal(io.getTerminal())
      .history(history)
      .completer(completer)
      .build();
  }

  public IO getIo() {
    return io;
  }

  public void setPrompt(final ConsolePrompt prompt) {
    this.prompt = prompt;
  }

  public ConsoleErrorHandler getErrorHandler() {
    return errorHandler;
  }

  public void setErrorHandler(final ConsoleErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public ConsoleTask getCurrentTask() {
    return currentTask;
  }

  public void close() {
    if (running) {
      log.trace("Closing");
      running = false;
    }
  }

  public void run() {
    log.trace("Running");
    running = true;

    while (running) {
      try {
        running = work();
      }
      catch (Throwable t) {
        log.trace("Work failed", t);

        if (getErrorHandler() != null) {
          running = getErrorHandler().handleError(t);
        }
        else {
          t.printStackTrace();
        }
      }
    }

    log.trace("Stopped");
  }

  protected ConsoleTask createTask() {
    try {
      return taskFactory.call();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Read and execute a line.
   *
   * @return False to abort, true to continue running.
   * @throws Exception Work failed.
   */
  private boolean work() throws Exception {
    String line = readLine(prompt != null ? prompt.prompt() : ConsolePrompt.DEFAULT_PROMPT);

    log.trace("Read line: {}", line);

    if (log.isTraceEnabled()) {
      traceLine(line);
    }

    if (line != null) {
      line = line.trim();
    }

    if (line == null || line.length() == 0) {
      return true;
    }

    // Build the task and execute it
    assert currentTask == null;
    currentTask = createTask();
    log.trace("Current task: {}", currentTask);

    try {
      return currentTask.execute(line);
    }
    finally {
      currentTask = null;
    }
  }

  private void traceLine(@Nullable final String line) {
    if (line == null) {
      return;
    }

    StringBuilder idx = new StringBuilder();
    StringBuilder hex = new StringBuilder();

    for (byte b : line.getBytes()) {
      String h = Integer.toHexString(b);

      hex.append('x').append(h).append(' ');
      idx.append(' ').append((char) b).append("  ");
    }

    log.trace("HEX: {}", hex);
    log.trace("     {}", idx);
  }

  private String readLine(@Nullable final String prompt) throws IOException {
    return lineReader.readLine(prompt);
  }

  private boolean interruptTask() throws Exception {
    boolean interrupt = false;

    ConsoleTask task = getCurrentTask();
    if (task != null) {
      synchronized (task) {
        log.debug("Interrupting task");
        interrupt = true;

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

    return interrupt;
  }
}
