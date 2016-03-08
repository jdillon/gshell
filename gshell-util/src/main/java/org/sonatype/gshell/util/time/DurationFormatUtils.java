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
package org.sonatype.gshell.util.time;

//
// NOTE: Copied and massaged from commons-lang 2.3
//

import java.util.ArrayList;

/**
 * Utilities for formatting a {@link Duration}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
class DurationFormatUtils
{
    public DurationFormatUtils() {
    }

    public static String formatDurationHMS(long durationMillis) {
        return formatDuration(durationMillis, "H:mm:ss.SSS", true);
    }

    private static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
        Token[] tokens = lexx(format);

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int milliseconds = 0;

        if (Token.containsTokenWithValue(tokens, d)) {
            days = (int) (durationMillis / DateUtils_MILLIS_PER_DAY);
            durationMillis = durationMillis - (days * DateUtils_MILLIS_PER_DAY);
        }
        if (Token.containsTokenWithValue(tokens, H)) {
            hours = (int) (durationMillis / DateUtils_MILLIS_PER_HOUR);
            durationMillis = durationMillis - (hours * DateUtils_MILLIS_PER_HOUR);
        }
        if (Token.containsTokenWithValue(tokens, m)) {
            minutes = (int) (durationMillis / DateUtils_MILLIS_PER_MINUTE);
            durationMillis = durationMillis - (minutes * DateUtils_MILLIS_PER_MINUTE);
        }
        if (Token.containsTokenWithValue(tokens, s)) {
            seconds = (int) (durationMillis / DateUtils_MILLIS_PER_SECOND);
            durationMillis = durationMillis - (seconds * DateUtils_MILLIS_PER_SECOND);
        }
        if (Token.containsTokenWithValue(tokens, S)) {
            milliseconds = (int) durationMillis;
        }

        return format(tokens, 0, 0, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    private static String format(Token[] tokens, int years, int months, int days, int hours, int minutes, int seconds, int milliseconds, boolean padWithZeros) {
        StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;
        int sz = tokens.length;
        for (int i = 0; i < sz; i++) {
            Token token = tokens[i];
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value.toString());
            }
            else {
                if (value == y) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(years), count, "0") : Integer.toString(years));
                    lastOutputSeconds = false;
                }
                else if (value == M) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(months), count, "0") : Integer.toString(months));
                    lastOutputSeconds = false;
                }
                else if (value == d) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(days), count, "0") : Integer.toString(days));
                    lastOutputSeconds = false;
                }
                else if (value == H) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(hours), count, "0") : Integer.toString(hours));
                    lastOutputSeconds = false;
                }
                else if (value == m) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(minutes), count, "0") : Integer.toString(minutes));
                    lastOutputSeconds = false;
                }
                else if (value == s) {
                    buffer.append(padWithZeros ? leftPad(Integer.toString(seconds), count, "0") : Integer.toString(seconds));
                    lastOutputSeconds = true;
                }
                else if (value == S) {
                    if (lastOutputSeconds) {
                        milliseconds += 1000;
                        String str = padWithZeros
                            ? leftPad(Integer.toString(milliseconds), count, "0")
                            : Integer.toString(milliseconds);
                        buffer.append(str.substring(1));
                    }
                    else {
                        buffer.append(padWithZeros
                            ? leftPad(Integer.toString(milliseconds), count, "0")
                            : Integer.toString(milliseconds));
                    }
                    lastOutputSeconds = false;
                }
            }
        }
        return buffer.toString();
    }

    private static final Object y = "y";
    private static final Object M = "M";
    private static final Object d = "d";
    private static final Object H = "H";
    private static final Object m = "m";
    private static final Object s = "s";
    private static final Object S = "S";

    private static Token[] lexx(String format) {
        char[] array = format.toCharArray();
        ArrayList<Token> list = new ArrayList<Token>(array.length);

        boolean inLiteral = false;
        StringBuilder buffer = null;
        Token previous = null;
        int sz = array.length;
        for (int i = 0; i < sz; i++) {
            char ch = array[i];
            if (inLiteral && ch != '\'') {
                buffer.append(ch);
                continue;
            }
            Object value = null;
            switch (ch) {
                // T O D O: Need to handle escaping of '
                case '\'':
                    if (inLiteral) {
                        buffer = null;
                        inLiteral = false;
                    }
                    else {
                        buffer = new StringBuilder();
                        list.add(new Token(buffer));
                        inLiteral = true;
                    }
                    break;
                case 'y':
                    value = y;
                    break;
                case 'M':
                    value = M;
                    break;
                case 'd':
                    value = d;
                    break;
                case 'H':
                    value = H;
                    break;
                case 'm':
                    value = m;
                    break;
                case 's':
                    value = s;
                    break;
                case 'S':
                    value = S;
                    break;
                default:
                    if (buffer == null) {
                        buffer = new StringBuilder();
                        list.add(new Token(buffer));
                    }
                    buffer.append(ch);
            }

            if (value != null) {
                if (previous != null && previous.getValue() == value) {
                    previous.increment();
                }
                else {
                    Token token = new Token(value);
                    list.add(token);
                    previous = token;
                }
                buffer = null;
            }
        }
        return list.toArray(new Token[list.size()]);
    }

    private static class Token
    {
        static boolean containsTokenWithValue(Token[] tokens, Object value) {
            int sz = tokens.length;
            for (int i = 0; i < sz; i++) {
                if (tokens[i].getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        private Object value;
        private int count;

        Token(Object value) {
            this.value = value;
            this.count = 1;
        }

        Token(Object value, int count) {
            this.value = value;
            this.count = count;
        }

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }

        Object getValue() {
            return value;
        }

        public boolean equals(Object obj2) {
            if (obj2 instanceof Token) {
                Token tok2 = (Token) obj2;
                if (this.value.getClass() != tok2.value.getClass()) {
                    return false;
                }
                if (this.count != tok2.count) {
                    return false;
                }
                if (this.value instanceof StringBuilder) {
                    return this.value.toString().equals(tok2.value.toString());
                }
                else if (this.value instanceof Number) {
                    return this.value.equals(tok2.value);
                }
                else {
                    return this.value == tok2.value;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return repeat(this.value.toString(), this.count);
        }
    }

    //
    // NOTE: Copied from plexus-utils StringUtils 1.5.5
    //

    private static String repeat(String str, int repeat) {
        StringBuilder buffer = new StringBuilder(repeat * str.length());
        for (int i = 0; i < repeat; i++) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    private static String leftPad(String str, int size, String delim) {
        size = (size - str.length()) / delim.length();
        if (size > 0) {
            str = repeat(delim, size) + str;
        }
        return str;
    }

    // Copied from commons-lang DateUtils 2.3

    private static final long DateUtils_MILLIS_PER_SECOND = 1000;

    private static final long DateUtils_MILLIS_PER_MINUTE = 60 * DateUtils_MILLIS_PER_SECOND;

    private static final long DateUtils_MILLIS_PER_HOUR = 60 * DateUtils_MILLIS_PER_MINUTE;

    private static final long DateUtils_MILLIS_PER_DAY = 24 * DateUtils_MILLIS_PER_HOUR;
}
