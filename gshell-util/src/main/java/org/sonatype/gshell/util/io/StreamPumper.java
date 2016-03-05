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
package org.sonatype.gshell.util.io;

import java.io.InputStream;
import java.io.OutputStream;

//
// Based on Apache Ant 1.6.5
//

/**
 * Copies all data from an input stream to an output stream.
 *
 * @since 2.0
 */
public class StreamPumper
    implements Runnable
{
    private final InputStream in;

    private final OutputStream out;

    private final boolean closeWhenExhausted;
    
    private volatile boolean finish;

    private volatile boolean finished;

    private boolean autoFlush;

    private Exception exception;

    private int bufferSize = 128;

    private boolean started;

    /**
     * Create a new stream pumper.
     *
     * @param in Input stream to read data from
     * @param out Output stream to write data to.
     * @param closeWhenExhausted If true, the output stream will be closed when the input is exhausted.
     */
    public StreamPumper(final InputStream in, final OutputStream out, final boolean closeWhenExhausted) {
        assert in != null;
        assert out != null;

        this.in = in;
        this.out = out;
        this.closeWhenExhausted = closeWhenExhausted;
    }

    /**
     * Create a new stream pumper.
     *
     * @param in Input stream to read data from
     * @param out Output stream to write data to.
     */
    public StreamPumper(final InputStream in, final OutputStream out) {
        this(in, out, false);
    }

    /**
     * Set whether data should be flushed through to the output stream.
     *
     * @param autoFlush If true, push through data; if false, let it be buffered
     */
    public void setAutoFlush(final boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    /**
     * Copies data from the input stream to the output stream.
     *
     * Terminates as soon as the input stream is closed or an error occurs.
     */
    public void run() {
        synchronized (this) {
            started = true;
        }
        finished = false;
        finish = false;

        final byte[] buf = new byte[bufferSize];

        int length;
        try {
            do {
                while (in.available() > 0 && !finish) {
                    length = in.read(buf);
                    if (length < 1 ) {
                        break;
                    }
                    out.write(buf, 0, length);
                    if (autoFlush) {
                        out.flush();
                    }
                }
                out.flush();
                Thread.sleep(200);  // Pause to avoid tight loop if external proc is slow
            }
            while (!finish && closeWhenExhausted);
        }
        catch (Exception e) {
            synchronized (this) {
                exception = e;
            }
        }
        finally {
            if (closeWhenExhausted) {
                Closer.close(out);
            }
            finished = true;

            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Tells whether the end of the stream has been reached.
     *
     * @return true If the stream has been exhausted.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * This method blocks until the stream pumper finishes.
     *
     * @see #isFinished()
     */
    public synchronized void waitFor() throws InterruptedException {
        while (!isFinished()) {
            wait();
        }
    }

    /**
     * Set the size in bytes of the read buffer.
     *
     * @param bufferSize the buffer size to use.
     * @throws IllegalStateException if the StreamPumper is already running.
     */
    public synchronized void setBufferSize(final int bufferSize) {
        if (started) {
            throw new IllegalStateException("Cannot set buffer size on a running StreamPumper");
        }

        this.bufferSize = bufferSize;
    }

    /**
     * Get the size in bytes of the read buffer.
     *
     * @return The size of the read buffer.
     */
    public synchronized int getBufferSize() {
        return bufferSize;
    }

    /**
     * Get the exception encountered, if any.
     *
     * @return The Exception encountered; or null if there was none.
     */
    public synchronized Exception getException() {
        return exception;
    }

    /**
     * Stop the pumper as soon as possible.
     *
     * Note that it may continue to block on the input stream
     * but it will really stop the thread as soon as it gets EOF
     * or any byte, and it will be marked as finished.
     */
    public synchronized void stop() {
        finish = true;
        notifyAll();
    }
}