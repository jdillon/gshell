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

package org.apache.maven.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellContext} thread context holder.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellContextHolder
{
    private static final Logger log = LoggerFactory.getLogger(ShellContextHolder.class);

    private static final InheritableThreadLocal<ShellContext> holder = new InheritableThreadLocal<ShellContext>();

    public static void set(final ShellContext context) {
        log.trace("Setting context: {}", context);

        holder.set(context);
    }

    public static ShellContext get(final boolean allowNull) {
        ShellContext context = holder.get();

        log.trace("Getting context ({}): {}", allowNull, context);

        if (!allowNull && context == null) {
            throw new IllegalStateException("Shell context not initialized for thread: " + Thread.currentThread());
        }

        return context;
    }

    public static ShellContext get() {
        return get(false);
    }
}