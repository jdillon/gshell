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

import jline.Terminal;
import jline.console.CandidateListCompletionHandler;
import jline.console.Completer;
import jline.console.ConsoleReader;
import jline.console.History;
import jline.console.history.MemoryHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

    private final ExecuteTaskFactory taskFactory;

    private final ConsoleReader reader;

    private final InputPipe pipe;

    private ConsolePrompt prompt;

    private ConsoleErrorHandler errorHandler;

    private ExecuteTask currentTask;

    private volatile boolean interrupt;

    private volatile boolean running;

    public Console(final ExecuteTaskFactory taskFactory, final IO io, final History history, final InputStream bindings) throws IOException {
        assert taskFactory != null;
        this.taskFactory = taskFactory;

        assert io != null;
        this.pipe = new InputPipe(io);

        this.reader = new ConsoleReader(
            pipe.getInputStream(),
            new PrintWriter(io.streams.out),
            bindings,
            io.getTerminal());

        this.reader.setPaginationEnabled(true);
        this.reader.setCompletionHandler(new CandidateListCompletionHandler());
        this.reader.setHistory(history != null ? history : new MemoryHistory());
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

    public ExecuteTask getCurrentTask() {
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
        running = true;
        pipe.start();

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
        currentTask = taskFactory.create();
        log.trace("Current task: {}", currentTask);

        try {
            return currentTask.execute(line);
        }
        finally {
            currentTask = null;
        }
    }

    private void traceLine(final String line) {
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

    private String readLine(final String prompt) throws IOException {
        // prompt may be null
        return reader.readLine(prompt);
    }

    private void checkTaskInterrupted() throws InterruptedIOException {
        if (interrupt) {
            interrupt = false;
            throw new InterruptedIOException("Keyboard interruption");
        }
    }

    private void interruptTask() {
        ExecuteTask task = getCurrentTask();

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
    }

    //
    // InputPipe
    //

    private class InputPipe
        extends Thread
    {
        private final Logger log = LoggerFactory.getLogger(getClass());

        private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1024);

        private final Terminal term;

        private final InputStream in;

        private final PrintStream err;

        private InputPipe(final IO io) {
            super("Console InputPipe");
            this.setDaemon(true);

            assert io != null;
            this.term = io.getTerminal();
            this.in = io.streams.in;
            this.err = io.streams.err;
        }

        private int read() throws IOException {
// FIXME: See if this is really needed and figure out why...
//            if (term instanceof AnsiWindowsTerminal) {
//                c = ((AnsiWindowsTerminal) term).readDirectChar(in);
//            }
//            else {
//                c = terminal.readCharacter(in);
//            }
            return term.readCharacter(in);
        }

        public void run() {
            log.trace("Running");

            try {
                while (running) {
                    int c = read();

                    switch (c) {
                        case -1:
                            queue.put(c);
                            return;

                        case 3:
                            err.println("^C");
                            reader.getCursorBuffer().clear();
                            interruptTask();
                            break;

                        case 4:
                            err.println("^D");
                            break;
                    }

                    queue.put(c);
                }
            }
            catch (Throwable t) {
                log.error("Pipe read failed", t);
                if (t instanceof RuntimeException) {
                    throw (RuntimeException)t;
                }
                else if (t instanceof Error) {
                    throw (Error)t;
                }
                else {
                    throw new Error(t);
                }
            }
            finally {
                close();
            }

            log.trace("Stopped");
        }

        public InputStream getInputStream() {
            return new PipeInputStream();
        }
        
        private class PipeInputStream
            extends InputStream
        {
            private int read(final boolean wait) throws IOException {
                if (!running) {
                    return -1;
                }
                checkTaskInterrupted();
                Integer i;
                if (wait) {
                    try {
                        i = queue.take();
                    }
                    catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                    checkTaskInterrupted();
                }
                else {
                    i = queue.poll();
                }
                if (i == null) {
                    return -1;
                }
                return i;
            }

            @Override
            public int read() throws IOException {
                return read(true);
            }

            @Override
            public int read(final byte b[], int off, final int len) throws IOException {
                if (b == null) {
                    throw new NullPointerException();
                }
                else if (off < 0 || len < 0 || len > b.length - off) {
                    throw new IndexOutOfBoundsException();
                }
                else if (len == 0) {
                    return 0;
                }

                int nb = 1;
                int i = read(true);
                if (i < 0) {
                    return -1;
                }
                b[off++] = (byte) i;
                while (nb < len) {
                    i = read(false);
                    if (i < 0) {
                        return nb;
                    }
                    b[off++] = (byte) i;
                    nb++;
                }
                return nb;
            }
        }
    }
}
