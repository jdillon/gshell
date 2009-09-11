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

package org.apache.maven.shell.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellContext} thread context holder.
 *
 * @version $Rev$ $Date$
 */
public class IOHolder
{
    private static final Logger log = LoggerFactory.getLogger(IOHolder.class);

    private static final InheritableThreadLocal<IO> holder = new InheritableThreadLocal<IO>();

    public static void set(final IO io) {
        log.trace("Setting IO: {}", io);

        holder.set(io);
    }

    public static IO get(final boolean allowNull) {
        IO io = holder.get();

        log.trace("Getting IO ({}): {}", allowNull, io);

        if (!allowNull && io == null) {
            throw new IllegalStateException("IO not initialized for thread: " + Thread.currentThread());
        }

        return io;
    }

    public static IO get() {
        return get(false);
    }
}