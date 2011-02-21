/**
 * Copyright (c) 2009-2011 the original author or authors.
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

import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.Completer;
import jline.console.ConsoleReader;
import jline.console.history.History;
import jline.console.history.MemoryHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.io.InputPipe;
import org.sonatype.gshell.util.io.StreamSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * Provides an abstraction of a console.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Console
    implements Runnable
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final InputPipe pipe;

    private final IO io;

    private final ConsoleReader reader;

    private final Callable<ConsoleTask> taskFactory;

    private ConsolePrompt prompt;

    private ConsoleErrorHandler errorHandler;

    private ConsoleTask currentTask;

    private volatile boolean running;

    public Console(final IO io, final Callable<ConsoleTask> taskFactory, final History history, final InputStream bindings) throws IOException {
        assert io != null;
        // history could be null
        // bindings could be null

        this.pipe = new InputPipe(io.streams, io.getTerminal(), new InputPipe.InterruptHandler() {
            public boolean interrupt() throws Exception {
                return interruptTask();
            }

            public boolean stop() throws Exception {
                return false;
            }
        });
        this.pipe.setName("Console InputPipe");
        this.pipe.setDaemon(true);

        // Setup a new IO w/our pipe input stream & rebuilding the input reader
        this.io = new IO(new StreamSet(pipe.getInputStream(), io.streams.out, io.streams.err), null, io.out, io.err, true);

        this.reader = new ConsoleReader(
            this.io.streams.in,
            this.io.out,
            bindings,
            io.getTerminal());

        this.reader.setPaginationEnabled(true);
        this.reader.setCompletionHandler(new CandidateListCompletionHandler());
        this.reader.setHistory(history != null ? history : new MemoryHistory());

        assert taskFactory != null;
        this.taskFactory = taskFactory;
    }

    public IO getIo() {
        return io;
    }

    public void addCompleter(final Completer completer) {
        assert completer != null;
        reader.addCompleter(completer);
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
            pipe.interrupt();
            running = false;
        }
    }

    public void run() {
        log.trace("Running");
        pipe.start();
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

                // Need to reset the terminal in some cases after a failure
                try {
                    io.getTerminal().reset();
                }
                catch (Exception e) {
                    log.error("Failed to reset terminal", e);
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

    private void traceLine(final String line) {
        if (line == null) return;

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

    private String readLine(final String prompt) throws IOException {
        // prompt may be null
        return reader.readLine(prompt);
    }

    private boolean interruptTask() throws Exception {
        boolean interrupt = false;

        reader.getCursorBuffer().clear();
        reader.redrawLine();

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
