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

import com.planet57.gshell.util.Notification;
import org.sonatype.goodies.common.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Encapsulates a console execute task.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class ConsoleTask
  extends ComponentSupport
{
  /**
   * The thread which is executing this task.
   */
  private Thread thread;

  /**
   * True if the task is running (ie. {@link #execute} was invoked).
   */
  private boolean running;

  /**
   * True if the task is stopping (ie. {@link #stop} was invoked).
   */
  private boolean stopping;

  private String input;

  /**
   * True if the task is running (ie. {@link #execute} was invoked).
   */
  public synchronized boolean isRunning() {
    return running;
  }

  /**
   * Ask the tasks execute thread to stop via {@link Thread#interrupt}.
   */
  public synchronized void stop() {
    if (running) {
      log.trace("Stopping");
      thread.interrupt();
      stopping = true;
    }
  }

  /**
   * True if {@link #stop} was invoked.
   */
  public synchronized boolean isStopping() {
    return stopping;
  }

  /**
   * Thrown to tasks which are asked to {link #abort}.
   */
  private static class AbortTaskNotification
    extends Notification
  {
    private static final long serialVersionUID = 1;
  }

  /**
   * Kill the tasks execute thread via {@link Thread#stop}.
   *
   * Thread is given an {@link AbortTaskNotification}.
   */
  @SuppressWarnings({"deprecation", "ThrowableInstanceNeverThrown"})
  public synchronized void abort() {
    if (running) {
      log.trace("Aborting");
      thread.stop(new AbortTaskNotification());
    }
  }

  /**
   * Execute a task for the given input.
   *
   * @param input The console input.
   * @return True to allow the console to continue, false to abort.
   * @throws Exception The console task failed.
   */
  public boolean execute(final String input) throws Exception {
    this.input = checkNotNull(input);

    synchronized (this) {
      log.trace("Running");
      thread = Thread.currentThread();
      running = true;
    }

    try {
      return doExecute(input);
    }
    finally {
      synchronized (this) {
        stopping = false;
        running = false;
        thread = null;
        log.trace("Stopped");
      }
    }
  }

  /**
   * Execute the custom task for the given input.
   *
   * @param input The console input.
   * @return True to allow the console to continue, false to abort.
   * @throws Exception The console task failed.
   */
  public abstract boolean doExecute(String input) throws Exception;

  @Override
  public String toString() {
    return "ConsoleTask{" +
      "thread=" + thread +
      ", running=" + running +
      ", stopping=" + stopping +
      ", input='" + input + '\'' +
      '}';
  }
}
