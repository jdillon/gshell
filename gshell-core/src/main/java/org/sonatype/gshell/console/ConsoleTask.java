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

/**
 * Encapsulates a console execute task.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class ConsoleTask
{
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTask.class);

    private Thread thread;

    private boolean running;

    private boolean stopping;

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void stop() {
        if (running) {
            log.trace("Stopping");
            thread.interrupt();
            stopping = true;
        }
    }

    public synchronized boolean isStopping() {
        return stopping;
    }

    public synchronized void abort() {
        if (running) {
            log.trace("Aborting");
            thread.stop(new AbortTaskError());
        }
    }

    public boolean execute(final String input) throws Exception {
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

    public abstract boolean doExecute(String input) throws Exception;

    public static class AbortTaskError
        extends Error
    {
        ///CLOVER:OFF

        private static final long serialVersionUID = 1;
    }
}