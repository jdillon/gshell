/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.command.resolver;

import static org.sonatype.gshell.command.resolver.Node.*;

/**
 * Representation of a {@link Node}'s path.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class NodePath
{
    private static final char SEPARATOR_CHAR = SEPARATOR.charAt(0);

    private static final char CURRENT_CHAR = CURRENT.charAt(0);

    private final StringBuilder path;

    public NodePath(final String path) {
        assert path != null;
        this.path = new StringBuilder(path);
    }

    public boolean isAbsolute() {
        return path.toString().startsWith(ROOT);
    }

    public String first() {
        String[] elements = split();
        return elements[0];
    }

    public String last() {
        String[] elements = split();
        return elements[elements.length - 1];
    }

    public NodePath parent() {
        int i = path.lastIndexOf(SEPARATOR);
        if (i == -1) {
            return this;
        }
        else {
            return new NodePath(path.substring(0, i));
        }
    }

    public NodePath normalize() {
        // Determine the start of the first element
        int startFirstElem = 0;

        if (path.charAt(0) == SEPARATOR_CHAR) {
            if (path.length() == 1) {
                return this;
            }
            startFirstElem = 1;
        }

        // Iterate over each element
        int startElem = startFirstElem;
        int maxLen = path.length();
        int regulars = 0;

        while (startElem < maxLen) {
            // Find the end of the element
            int endElem = startElem;
            for (; endElem < maxLen && path.charAt(endElem) != SEPARATOR_CHAR; endElem++) {
                // empty
            }

            final int elemLen = endElem - startElem;

            if (regulars > 0 && elemLen == 0) {
                // An empty element - axe it
                path.delete(endElem, endElem + 1);
                maxLen = path.length();
                continue;
            }

            if (regulars > 0 && elemLen == 1 && path.charAt(startElem) == CURRENT_CHAR) {
                // A '.' element - axe it
                path.delete(startElem, endElem + 1);
                maxLen = path.length();
                continue;
            }

            if (elemLen == 2 && path.charAt(startElem) == CURRENT_CHAR && path.charAt(startElem + 1) == CURRENT_CHAR) {
                // A '..' element - remove the previous element if there is a regular element to remove
                if (regulars > 0 && startElem != startFirstElem) {
                    // Find start of previous element
                    int pos = startElem - 2;
                    for (; pos >= 0 && path.charAt(pos) != SEPARATOR_CHAR; pos--) {
                        // empty
                    }
                    startElem = pos + 1;
                    path.delete(startElem, endElem + 1);
                    maxLen = path.length();
                }
                else {
                    // if there are no more regulars, then consume the .. and move on
                    startElem = startElem + 3;
                }
                continue;
            }

            // A regular element
            startElem = endElem + 1;
            regulars++;
        }

        return this;
    }

    public String[] split() {
        String[] elements = toString().split(SEPARATOR);
        if (isAbsolute()) {
            if (elements.length == 0) {
                elements = new String[1];
            }
            elements[0] = ROOT;
        }
        return elements;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return path.toString();
    }
}