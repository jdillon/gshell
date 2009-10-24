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

package org.sonatype.gshell.ansi;

import org.fusesource.jansi.Ansi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders ANSI color escape-codes in strings by parsing out some special syntax to pick up the correct fluff to use.
 *
 * <p>
 * The syntax for embedded ANSI codes is:
 * 
 * <pre>
 *     @|<code>(,<code>)*<space><text>|
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
            Code code = Code.valueOf(name.toUpperCase());

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

    public static enum Code
    {
        // Colors
        BLACK(org.fusesource.jansi.Ansi.Color.BLACK),
        RED(Ansi.Color.RED),
        GREEN(Ansi.Color.GREEN),
        YELLOW(Ansi.Color.YELLOW),
        BLUE(Ansi.Color.BLUE),
        MAGENTA(Ansi.Color.MAGENTA),
        CYAN(Ansi.Color.CYAN),
        WHITE(Ansi.Color.WHITE),

        // Forground Colors
        FG_BLACK(Ansi.Color.BLACK, false),
        FG_RED(Ansi.Color.RED, false),
        FG_GREEN(Ansi.Color.GREEN, false),
        FG_YELLOW(Ansi.Color.YELLOW, false),
        FG_BLUE(Ansi.Color.BLUE, false),
        FG_MAGENTA(Ansi.Color.MAGENTA, false),
        FG_CYAN(Ansi.Color.CYAN, false),
        FG_WHITE(Ansi.Color.WHITE, false),

        // Background Colors
        BG_BLACK(Ansi.Color.BLACK, true),
        BG_RED(Ansi.Color.RED, true),
        BG_GREEN(Ansi.Color.GREEN, true),
        BG_YELLOW(Ansi.Color.YELLOW, true),
        BG_BLUE(Ansi.Color.BLUE, true),
        BG_MAGENTA(Ansi.Color.MAGENTA, true),
        BG_CYAN(Ansi.Color.CYAN, true),
        BG_WHITE(Ansi.Color.WHITE, true),

        // Attributes
        RESET(Ansi.Attribute.RESET),
        INTENSITY_BOLD(Ansi.Attribute.INTENSITY_BOLD),
        INTENSITY_FAINT(Ansi.Attribute.INTENSITY_FAINT),
        ITALIC(Ansi.Attribute.ITALIC),
        UNDERLINE(Ansi.Attribute.UNDERLINE),
        BLINK_SLOW(Ansi.Attribute.BLINK_SLOW),
        BLINK_FAST(Ansi.Attribute.BLINK_FAST),
        BLINK_OFF(Ansi.Attribute.BLINK_OFF),
        NEGATIVE_ON(Ansi.Attribute.NEGATIVE_ON),
        NEGATIVE_OFF(Ansi.Attribute.NEGATIVE_OFF),
        CONCEAL_ON(Ansi.Attribute.CONCEAL_ON),
        CONCEAL_OFF(Ansi.Attribute.CONCEAL_OFF),
        UNDERLINE_DOUBLE(Ansi.Attribute.UNDERLINE_DOUBLE),
        INTENSITY_NORMAL(Ansi.Attribute.INTENSITY_NORMAL),
        UNDERLINE_OFF(Ansi.Attribute.UNDERLINE_OFF),

        // Aliases
        BOLD(Ansi.Attribute.INTENSITY_BOLD),
        FAINT(Ansi.Attribute.INTENSITY_FAINT),
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
            return n instanceof Ansi.Color;
        }

        public Ansi.Color getColor() {
            return (Ansi.Color) n;
        }

        public boolean isAttribute() {
            return n instanceof Ansi.Attribute;
        }

        public Ansi.Attribute getAttribute() {
            return (Ansi.Attribute) n;
        }

        public boolean isBackground() {
            return background;
        }
    }
}
