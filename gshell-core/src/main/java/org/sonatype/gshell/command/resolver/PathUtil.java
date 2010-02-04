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

import static org.sonatype.gshell.command.resolver.CommandResolver.CURRENT_CHAR;
import static org.sonatype.gshell.command.resolver.CommandResolver.ROOT;
import static org.sonatype.gshell.command.resolver.CommandResolver.SEPARATOR;
import static org.sonatype.gshell.command.resolver.CommandResolver.SEPARATOR_CHAR;

/**
 * Node path utilities.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class PathUtil
{
    //
    // NOTE: normalize() is adapted from Commons VFS's UriParser.normalisePath()
    //
    
    public static StringBuilder normalize(final StringBuilder path) {
        assert path != null;

        // Determine the start of the first element
        int startFirstElem = 0;

        if (path.charAt(0) == SEPARATOR_CHAR) {
            if (path.length() == 1) {
                return path;
            }
            startFirstElem = 1;
        }

        // Iterate over each element
        int startElem = startFirstElem;
        int maxLen = path.length();

        while (startElem < maxLen) {
            // Find the end of the element
            int endElem = startElem;
            for (; endElem < maxLen && path.charAt(endElem) != SEPARATOR_CHAR; endElem++) {
                // empty
            }

            final int elemLen = endElem - startElem;
            if (elemLen == 0) {
                // An empty element - axe it
                path.delete(endElem, endElem + 1);
                maxLen = path.length();
                continue;
            }

            if (elemLen == 1 && path.charAt(startElem) == CURRENT_CHAR) {
                // A '.' element - axe it
                path.delete(startElem, endElem + 1);
                maxLen = path.length();
                continue;
            }

            if (elemLen == 2 && path.charAt(startElem) == CURRENT_CHAR && path.charAt(startElem + 1) == CURRENT_CHAR) {
                // A '..' element - remove the previous element
                if (startElem != startFirstElem) {
                    // Find start of previous element
                    int pos = startElem - 2;
                    for (; pos >= 0 && path.charAt(pos) != SEPARATOR_CHAR; pos--) {
                        // empty
                    }
                    startElem = pos + 1;
                }

                path.delete(startElem, endElem + 1);
                maxLen = path.length();
                continue;
            }

            // A regular element
            startElem = endElem + 1;
        }

        return path;
    }

    public static String normalize(final String path) {
        assert path != null;
        return normalize(new StringBuilder(path)).toString();
    }

    public static String[] split(String path) {
        assert path != null;
        String[] elements = normalize(path).split(SEPARATOR);
        if (isAbsolute(path)) {
            if (elements.length == 0) {
                elements = new String[1];
            }
            elements[0] = ROOT;
        }
        return elements;
    }

    public static boolean isAbsolute(final String path) {
        assert path != null;
        return path.startsWith(ROOT);
    }
}