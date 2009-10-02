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

package org.apache.gshell.ansi;

import jline.Terminal;

/**
 * Provides support for using ANSI color escape codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class Ansi
    extends org.fusesource.jansi.Ansi
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

    //
    // JAnsi support
    //

    public static org.fusesource.jansi.Ansi ansi() {
        if (isEnabled()) {
            return new Ansi();
        }
        else {
            return new NoAnsi();
        }
    }

    private static class NoAnsi
        extends org.fusesource.jansi.Ansi
    {
        @Override
        public org.fusesource.jansi.Ansi a(Attribute attribute) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi fg(Color color) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi bg(Color color) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi reset() {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi cursor(int i, int i1) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi cursorDown(int i) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi cursorLeft(int i) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi cursorRight(int i) {
            return this;
        }

        @Override
        public org.fusesource.jansi.Ansi cursorUp(int i) {
            return this;
        }

        @Override
        protected boolean isAttributeAnsi() {
            return false;
        }

        @Override
        protected boolean isStringBuilderAnsi() {
            return false;
        }
    }
}
