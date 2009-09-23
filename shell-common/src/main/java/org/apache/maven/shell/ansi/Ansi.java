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

package org.apache.maven.shell.ansi;

import jline.Terminal;

/**
 * Provides support for using ANSI color escape codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public class Ansi
{
    /**
     * Tries to detect if the current system supports ANSI.
     */
    private static boolean detect() {
        boolean enabled = Terminal.getTerminal().isANSISupported();

        if (!enabled) {
            enabled = Boolean.getBoolean(Ansi.class.getName() + ".force");
        }

        return enabled;
    }

    public static boolean isDetected() {
        return detect();
    }

    private static final InheritableThreadLocal<Boolean> holder = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return isDetected();
        }
    };
    
    public static void setEnabled(final boolean flag) {
        holder.set(flag);
    }

    public static boolean isEnabled() {
        return holder.get();
    }
}
