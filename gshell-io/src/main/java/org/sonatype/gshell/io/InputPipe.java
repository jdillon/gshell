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

package org.sonatype.gshell.io;

import jline.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * An input pipe which can be interrupted.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class InputPipe
    extends Thread
    implements Closeable
{
    private static final Logger log = LoggerFactory.getLogger(InputPipe.class);

    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1024);

    private final Terminal term;

    private final StreamSet streams;

    public static interface InterruptHandler
    {
        boolean interrupt() throws Exception;

        boolean stop() throws Exception;
    }

    private final InterruptHandler interruptHandler;

    private final CountDownLatch startSignal = new CountDownLatch(1);

    private volatile boolean interrupt;

    private volatile boolean running;

    public InputPipe(final StreamSet streams, final Terminal terminal, final InterruptHandler interruptHandler) {
        assert streams != null;
        this.streams = streams;
        this.term = terminal;
        assert interruptHandler != null;
        this.interruptHandler = interruptHandler;
    }

    public void close() {
        if (running) {
            log.trace("Closing");
            Thread.currentThread().interrupt();
            running = false;
        }
    }
    
    private int read() throws IOException {
        return term.readCharacter(streams.in);
    }

    @Override
    public void start() {
        super.start();

        // Wait for the run-loop to actually start before we return, to avoid race-conditions
        try {
            startSignal.await();
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

    public void run() {
        log.trace("Running");
        running = true;

        try {
            startSignal.countDown();
            
            while (running) {
                try {
                    int c = read();

                    switch (c) {
                        case -1:
                            queue.put(c);
                            return;

                        case 3: // CTRL-C
                            interrupt = interruptHandler.interrupt();
                            break;

//                        case 4: // CTRL-D
//                            running = interruptHandler.stop();
//                            break;
                    }

                    queue.put(c);
                }
                catch (IOException e) {
                    log.warn("Pipe read error", e);

                    // HACK: Reset the terminal
                    term.restore();
                    term.init();
                }
            }
        }
        catch (Throwable t) {
            log.error("Pipe read failure", t);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            else if (t instanceof Error) {
                throw (Error) t;
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
        private void checkInterrupted() throws InterruptedIOException {
            if (interrupt) {
                interrupt = false;
                throw new InterruptedIOException("Keyboard interruption");
            }
        }

        private int read(final boolean wait) throws IOException {
            if (!running) {
                return -1;
            }

            checkInterrupted();

            Integer i;
            if (wait) {
                try {
                    // Wait for the pipe to actually start consuming bytes before we start taking
                    startSignal.await();

                    // Take a byte for the queue
                    i = queue.take();
                }
                catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
                
                checkInterrupted();
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