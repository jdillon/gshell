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

/**
 * <tt>ANSI</tt> color codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public enum AnsiCode
{
    OFF(0),
    BOLD(1),
    UNDERSCORE(4),
    BLINK(5),
    REVERSE(7),
    CONCEALED(8),

    FG_BLACK(30),
    FG_RED(31),
    FG_GREEN(32),
    FG_YELLOW(33),
    FG_BLUE(34),
    FG_MAGENTA(35),
    FG_CYAN(36),
    FG_WHITE(37),

    BLACK(FG_BLACK),
    RED(FG_RED),
    GREEN(FG_GREEN),
    YELLOW(FG_YELLOW),
    BLUE(FG_BLUE),
    MAGENTA(FG_MAGENTA),
    CYAN(FG_CYAN),
    WHITE(FG_WHITE),

    BG_BLACK(40),
    BG_RED(41),
    BG_GREEN(42),
    BG_YELLOW(43),
    BG_BLUE(44),
    BG_MAGENTA(45),
    BG_CYAN(46),
    BG_WHITE(47);

    /** The ANSI escape char which is used to start sequences. */
    private static final char ESC = 27;

    private final int code;

    private AnsiCode(final int code) {
        this.code = code;
    }

    private AnsiCode(final AnsiCode code) {
        this(code.code);
    }
    
    public static String attrib(final AnsiCode code) {
        assert code != null;

        StringBuilder buff = new StringBuilder();
        buff.append(ESC).append('[').append(code.code).append('m');
        return buff.toString();
    }
}
