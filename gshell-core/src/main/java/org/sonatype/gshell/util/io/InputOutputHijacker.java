/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sonatype.gshell.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * Hijacks the systems standard output and error streams on a per-thread basis
 * and redirects to given streams.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class InputOutputHijacker
{
    private static Logger log = LoggerFactory.getLogger(InputOutputHijacker.class);

    /**
     * Contains a {@link StreamRegistration} for the current thread if its registered, else null.
     */
    private static final
    InheritableThreadLocal<StreamRegistration>
        registrations =
        new InheritableThreadLocal<StreamRegistration>();

    /**
     * The previously installed System streams, initialized when installing.
     */
    private static StreamSet previous;

    /**
     * Flag to indicate if the hijacker is installed or not.
     */
    private static boolean installed;

    /**
     * Check if the hijacker has been installed.
     */
    public static synchronized boolean isInstalled() {
        return installed;
    }

    private static synchronized void ensureInstalled() {
        if (!isInstalled()) {
            throw new IllegalStateException("Not installed");
        }
    }

    /**
     * Install the hijacker.
     */
    public static synchronized void install() {
        if (installed) {
            throw new IllegalStateException("Already installed");
        }

        // Capture the current set of streams
        previous = new StreamSet(System.in, System.out, System.err);

        // Install our streams
        System.setIn(new DelegateInputStream());
        System.setOut(new DelegateOutputStream(StreamSet.OutputType.OUT));
        System.setErr(new DelegateOutputStream(StreamSet.OutputType.ERR));

        installed = true;

        log.debug("Installed");
    }

    /**
     * Install the hijacker and register streams for the current thread.
     */
    public static synchronized void install(final InputStream in, final PrintStream out, final PrintStream err) {
        install();
        register(in, out, err);
    }

    /**
     * Install the hijacker and register combined streams for the current thread.
     */
    public static synchronized void install(final InputStream in, final PrintStream out) {
        install();
        register(in, out);
    }

    /**
     * Install the hijacker and register streams for the current thread.
     */
    public static synchronized void install(final StreamSet set) {
        install();
        register(set);
    }

    public static synchronized void maybeInstall() {
        if (!isInstalled()) {
            install();
        }
    }

    public static synchronized void maybeInstall(final StreamSet set) {
        if (!isInstalled()) {
            install(set);
        }
    }

    /**
     * Uninstall the hijacker.
     */
    public static synchronized void uninstall() {
        ensureInstalled();

        System.setIn(previous.in);
        System.setOut(previous.out);
        System.setErr(previous.err);

        previous = null;
        installed = false;

        log.debug("Uninstalled");
    }

    /**
     * Get the current stream registration.
     */
    private static synchronized StreamRegistration registration(final boolean required) {
        if (required) {
            ensureRegistered();
        }

        return registrations.get();
    }

    /**
     * Check if there are streams registered for the current thread.
     */
    public static synchronized boolean isRegistered() {
        return registration(false) != null;
    }

    private static synchronized void ensureRegistered() {
        ensureInstalled();

        if (!isRegistered()) {
            throw new IllegalStateException(MessageFormat.format("Not registered: {0}", Thread.currentThread()));
        }
    }

    /**
     * Register streams for the current thread.
     */
    public static synchronized void register(final InputStream in, final PrintStream out, final PrintStream err) {
        ensureInstalled();

        if (log.isTraceEnabled()) {
            log.trace("Registering: {} -> {}, {}, {}", new Object[]{Thread.currentThread(), in, out, err});
        }

        StreamRegistration prev = registration(false);
        StreamSet set = new StreamSet(in, out, err);
        StreamRegistration next = new StreamRegistration(set, prev);

        registrations.set(next);
    }

    /**
     * Register combinded streams for the current thread.
     */
    public static synchronized void register(final InputStream in, final PrintStream out) {
        register(in, out, out);
    }

    /**
     * Register streams for the current thread.
     */
    public static synchronized void register(final StreamSet set) {
        assert set != null;

        register(set.in, set.out, set.err);
    }

    /**
     * Reregister streams for the current thread, and restore the previous if any.
     */
    public static synchronized void deregister() {
        StreamRegistration cur = registration(true);

        registrations.set(cur.previous);

        log.trace("Deregistered: {}", Thread.currentThread());
    }

    /**
     * Stream registration information.
     */
    private static class StreamRegistration
    {
        public final StreamSet streams;

        public final StreamRegistration previous;

        public StreamRegistration(final StreamSet streams, final StreamRegistration previous) {
            assert streams != null;

            this.streams = streams;
            this.previous = previous;
        }
    }

    /**
     * Returns the currently registered streams.
     */
    private static synchronized StreamSet current() {
        StreamRegistration reg = registration(false);
        if (reg == null) {
            return previous;
        }
        return reg.streams;
    }

    //
    // System Stream Restoration.
    //

    /**
     * Restores the System streams to the given pair and resets the hijacker state to uninstalled.
     */
    public static synchronized void restore(final StreamSet streams) {
        assert streams != null;

        StreamSet.system(streams);

        previous = null;
        installed = false;
    }

    /**
     * Restores the original System streams from {@link StreamSet#SYSTEM} and resets
     * the hijacker state to uninstalled.
     */
    public static synchronized void restore() {
        restore(StreamSet.SYSTEM);
    }

    /**
     * Delegates write calls to the currently registered output stream.
     */
    private static class DelegateOutputStream
        extends PrintStream
    {
        private static final ByteArrayOutputStream NULL_OUTPUT = new ByteArrayOutputStream();

        private final StreamSet.OutputType type;

        public DelegateOutputStream(final StreamSet.OutputType type) {
            super(NULL_OUTPUT);

            assert type != null;

            this.type = type;
        }

        private PrintStream get() {
            return current().getOutput(type);
        }

        @Override
        public void write(final int b) {
            get().write(b);
        }

        @Override
        public void write(final byte b[]) throws IOException {
            get().write(b, 0, b.length);
        }

        @Override
        public void write(final byte[] b, final int off, final int len) {
            get().write(b, off, len);
        }

        @Override
        public void flush() {
            get().flush();
        }

        @Override
        public void close() {
            get().close();
        }
    }

    /**
     * Delegates read calls to the currently registered input stream.
     */
    private static class DelegateInputStream
        extends InputStream
    {
        public DelegateInputStream() {
            super();
        }

        private InputStream get() {
            return current().getInput();
        }

        @Override
        public int read() throws IOException {
            return get().read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return get().read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return get().read(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            return get().skip(n);
        }

        @Override
        public int available() throws IOException {
            return get().available();
        }

        @Override
        public void close() throws IOException {
            get().close();
        }

        @Override
        public void mark(final int readlimit) {
            get().mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            get().reset();
        }

        @Override
        public boolean markSupported() {
            return get().markSupported();
        }
    }
}