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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class AnsiRenderer
{
    public static final String BEGIN_TOKEN = "@|";

    public static final String END_TOKEN = "|";

    public static final String CODE_TEXT_SEPARATOR  = " ";

    public static final String CODE_LIST_SEPARATOR  = ",";

    private static final Pattern PATTERN = Pattern.compile("\\@\\|([^ ]+) ([^|]+)\\|");

    public String render(String input) {
        if (input != null) {
            Matcher matcher = PATTERN.matcher(input);

            while (matcher.find()) {
                String rep = render(matcher.group(2), matcher.group(1).split(CODE_LIST_SEPARATOR));
                if (rep != null) {
                    input = input.replace(matcher.group(0), rep);
                    matcher.reset(input);
                }
            }
        }

        return input;
    }

    private String render(final String text, final String... codes) {
        org.fusesource.jansi.Ansi ansi = Ansi.ansi();

        for (String name : codes) {
            Ansi.Code code = Ansi.Code.valueOf(name.toUpperCase());

            if (code.isColor()) {
                if (code.isBackground()) {
                    ansi = ansi.bg(code.getColor());
                }
                else {
                    ansi = ansi.fg(code.getColor());
                }
            }
            else if (code.isAttribute()) {
                ansi = ansi.a(code.getAttribute());
            }
        }

        return ansi.a(text).reset().toString();
    }

    public static boolean test(final String text) {
        return text != null && text.contains(BEGIN_TOKEN);
    }
}
