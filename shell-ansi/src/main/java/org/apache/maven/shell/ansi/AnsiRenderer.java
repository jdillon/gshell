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
 * Renders ANSI color escape-codes in strings by parsing out some special syntax to pick up the correct fluff to use.
 *
 * <p>
 * The syntax for embedded ANSI codes is:
 * 
 * <pre>
 *  @|<code>(,<code>)*<space><text>|
 * </pre>
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRenderer
{
    public static final String BEGIN_TOKEN = "@|";

    private static final int BEGIN_TOKEN_SIZE = BEGIN_TOKEN.length();

    public static final String END_TOKEN = "|";

    private static final int END_TOKEN_SIZE = END_TOKEN.length();

    public static final String CODE_TEXT_SEPARATOR  = " ";

    public static final String CODE_LIST_SEPARATOR  = ",";

    private final AnsiBuffer buff = new AnsiBuffer();

    public String render(final String input) throws RenderException {
        assert input != null;

        // current, prefix and suffix positions
        int c = 0, p, s;

        while (c < input.length()) {
            p = input.indexOf(BEGIN_TOKEN, c);
            if (p < 0) { break; }

            s = input.indexOf(END_TOKEN, p + BEGIN_TOKEN_SIZE);
            if (s < 0) {
                throw new RenderException("Missing '" + END_TOKEN + "': " + input);
            }

            String expr = input.substring(p + BEGIN_TOKEN_SIZE, s);

            buff.append(input.substring(c, p));

            evaluate(expr);

            c = s + END_TOKEN_SIZE;
        }

        buff.append(input.substring(c));

        return buff.toString();
    }

    private void evaluate(final String input) throws RenderException {
        assert input != null;

        int i = input.indexOf(CODE_TEXT_SEPARATOR);
        if (i < 0) {
            throw new RenderException("Missing ANSI code/text separator '" + CODE_TEXT_SEPARATOR + "': " + input);
        }

        String tmp = input.substring(0, i);
        String[] codes = tmp.split(CODE_LIST_SEPARATOR);
        String text = input.substring(i + 1, input.length());

        for (String name : codes) {
            AnsiCode code = AnsiCode.valueOf(name.toUpperCase());
            buff.attrib(code);
        }

        buff.append(text);

        buff.attrib(AnsiCode.OFF);
    }

    //
    // RenderException
    //

    public static class RenderException
        extends RuntimeException
    {
        public RenderException(final String msg) {
            super(msg);
        }
    }

    //
    // Helpers
    //

    public static boolean test(final String text) {
        return text != null && text.indexOf(BEGIN_TOKEN) >= 0;
    }

    public static String encode(final Object text, final AnsiCode code) {
        return new StringBuilder(BEGIN_TOKEN).
                append(code.name()).
                append(CODE_TEXT_SEPARATOR).
                append(text).
                append(END_TOKEN).
                toString();
    }
}
