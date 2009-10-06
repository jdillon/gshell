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

import jline.TerminalFactory;
import org.apache.gshell.internal.Log;

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
    @SuppressWarnings({ "StringConcatenation" })
    public static final String FORCE = Ansi.class.getName() + ".force";

    /**
     * Tries to detect if the current system supports ANSI.
     */
    private static boolean detect() {
        boolean enabled = TerminalFactory.get().isAnsiSupported();

        Log.trace("ANSI Detected: ", enabled);

        if (!enabled) {
            enabled = Boolean.getBoolean(FORCE);
            Log.trace("ANSI Forced: ", enabled);
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

    public static enum Code
    {
        // Colors
        BLACK(Color.BLACK),
        RED(Color.RED),
        GREEN(Color.GREEN),
        YELLOW(Color.YELLOW),
        BLUE(Color.BLUE),
        MAGENTA(Color.MAGENTA),
        CYAN(Color.CYAN),
        WHITE(Color.WHITE),

        // Forground Colors
        FG_BLACK(Color.BLACK, false),
        FG_RED(Color.RED, false),
        FG_GREEN(Color.GREEN, false),
        FG_YELLOW(Color.YELLOW, false),
        FG_BLUE(Color.BLUE, false),
        FG_MAGENTA(Color.MAGENTA, false),
        FG_CYAN(Color.CYAN, false),
        FG_WHITE(Color.WHITE, false),

        // Background Colors
        BG_BLACK(Color.BLACK, true),
        BG_RED(Color.RED, true),
        BG_GREEN(Color.GREEN, true),
        BG_YELLOW(Color.YELLOW, true),
        BG_BLUE(Color.BLUE, true),
        BG_MAGENTA(Color.MAGENTA, true),
        BG_CYAN(Color.CYAN, true),
        BG_WHITE(Color.WHITE, true),

        // Attributes
        RESET(Attribute.RESET),
        INTENSITY_BOLD(Attribute.INTENSITY_BOLD),
        INTENSITY_FAINT(Attribute.INTENSITY_FAINT),
        ITALIC(Attribute.ITALIC),
        UNDERLINE(Attribute.UNDERLINE),
        BLINK_SLOW(Attribute.BLINK_SLOW),
        BLINK_FAST(Attribute.BLINK_FAST),
        BLINK_OFF(Attribute.BLINK_OFF),
        NEGATIVE_ON(Attribute.NEGATIVE_ON),
        NEGATIVE_OFF(Attribute.NEGATIVE_OFF),
        CONCEAL_ON(Attribute.CONCEAL_ON),
        CONCEAL_OFF(Attribute.CONCEAL_OFF),
        UNDERLINE_DOUBLE(Attribute.UNDERLINE_DOUBLE),
        INTENSITY_NORMAL(Attribute.INTENSITY_NORMAL),
        UNDERLINE_OFF(Attribute.UNDERLINE_OFF),

        // Aliases
        BOLD(Attribute.INTENSITY_BOLD),
        FAINT(Attribute.INTENSITY_FAINT),
        ;

        private final Enum n;

        private final boolean background;

        private Code(final Enum n, boolean background) {
            this.n = n;
            this.background = background;
        }

        private Code(final Enum n) {
            this(n, false);
        }

        public boolean isColor() {
            return n instanceof Color;
        }

        public Color getColor() {
            return (Color) n;
        }

        public boolean isAttribute() {
            return n instanceof Attribute;
        }

        public Attribute getAttribute() {
            return (Attribute) n;
        }

        public boolean isBackground() {
            return background;
        }
    }
}
