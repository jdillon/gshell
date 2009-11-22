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

package org.sonatype.gshell.artifact.monitor;

/**
 * Provides a fancy text-based progress spinner w/optional informative message.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ProgressSpinner
{
    private static final String MESSAGE_PAD = " ";

    private final Style style;

    private int state = 0;

    //
    // TODO: Add a timer to spin the progress
    //

    public ProgressSpinner(final Style style) {
        assert style != null;
        this.style = style;
    }

    public ProgressSpinner() {
        this(new DefaultStyle());
    }

    public void reset() {
        state = 0;
    }

    public String spin() {
        return spin(null);
    }

    public void stop() {
        // TODO: ???
    }

    public String spin(final String message) {
        // message may be null

        StringBuilder buff = new StringBuilder();

        String[] elements = style.getElements();
        buff.append(style.getPrefix());
        buff.append(elements[state]);
        buff.append(style.getSuffix());

        if (message != null) {
            buff.append(MESSAGE_PAD);
            buff.append(message);
        }

        state++;
        if (state >= elements.length) {
            state = 0;
        }

        return buff.toString();
    }

    //
    // Style
    //

    public static interface Style
    {
        String getPrefix();

        String[] getElements();

        String getSuffix();
    }

    //
    // Default Style
    //

    public static class DefaultStyle
        implements Style
    {
        private static final String[] ELEMENTS = {
            "|",
            "/",
            "-",
            "\\",
        };

        public String getPrefix() {
            return "(";
        }

        public String[] getElements() {
            return ELEMENTS;
        }

        public String getSuffix() {
            return ")";
        }
    }
}