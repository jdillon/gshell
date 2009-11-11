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
 * Holder for the current {@link Console} execute thread.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ExecuteThread
{
    private static final Logger log = LoggerFactory.getLogger(ExecuteThread.class);

    private static final InheritableThreadLocal<Thread> holder = new InheritableThreadLocal<Thread>();

    public static Thread set(final Thread thread) {
        log.trace("Setting execute thread: {}", thread);

        Thread last = holder.get();

        holder.set(thread);

        return last;
    }

    public static Thread get(final boolean allowNull) {
        Thread thread = holder.get();

        log.trace("Getting execute thread ({}): {}", allowNull, thread);

        if (!allowNull && thread == null) {
            throw new IllegalStateException("Execute thread not initialized for thread: " + Thread.currentThread());
        }

        return thread;
    }

    public static Thread get() {
        return get(false);
    }
}
