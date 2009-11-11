/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Provides an abstraction of a console.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class Console
    implements Runnable
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ConsolePrompt prompt = new DefaultPrompt();

    protected ConsoleErrorHandler errorHandler = new DefaultErrorHandler();

    protected ExecuteTaskFactory taskFactory;

    protected ExecuteTask currentTask;

    protected boolean running = false;

    protected boolean breakOnNull = true;

    protected boolean autoTrim = true;

    protected boolean ignoreEmpty = true;

    protected Console(final ExecuteTaskFactory taskFactory) {
        assert taskFactory != null;
        this.taskFactory = taskFactory;
    }

    public ConsolePrompt getPrompt() {
        return prompt;
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

    public ExecuteTaskFactory getTaskFactory() {
        return taskFactory;
    }

    public void setTaskFactory(final ExecuteTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public boolean isBreakOnNull() {
        return breakOnNull;
    }

    public void setBreakOnNull(final boolean breakOnNull) {
        this.breakOnNull = breakOnNull;
    }

    public boolean isAutoTrim() {
        return autoTrim;
    }

    public void setAutoTrim(final boolean autoTrim) {
        this.autoTrim = autoTrim;
    }

    public boolean isIgnoreEmpty() {
        return ignoreEmpty;
    }

    public void setIgnoreEmpty(final boolean ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
    }

    public ExecuteTask getCurrentTask() {
        return currentTask;
    }

    public void close() {
        log.trace("Closing");
        running = false;
    }

    public void run() {
        log.trace("Running");

        assert prompt != null;
        assert taskFactory != null;

        running = true;

        while (running) {
            try {
                running = work();
            }
            catch (Throwable t) {
                // Don't use {} here so we get the throwable detail in the log stream
                log.trace("Work failed", t);

                if (getErrorHandler() != null) {
                    running = getErrorHandler().handleError(t);
                }
            }
        }

        log.trace("Finished");
    }

    /**
     * @return  False to abort, true to continue running.
     */
    protected boolean work() throws Exception {
        String line = readLine(prompt.prompt());

        log.trace("Read line: {}", line);

        if (log.isTraceEnabled()) {
            traceLine(line);
        }

        if (line == null) {
            return !breakOnNull;
        }

        if (autoTrim) {
            line = line.trim();
        }

        if (ignoreEmpty && line.length() == 0) {
            return true;
        }

        // Build the task and execute it
        assert currentTask == null;
        currentTask = taskFactory.create();
        log.trace("Current task: {}", currentTask);

        try {
            return currentTask.execute(line);
        }
        finally {
            currentTask = null;
        }
    }

    protected void traceLine(final String line) {
        assert line != null;

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

    protected abstract String readLine(String prompt) throws IOException;
}
