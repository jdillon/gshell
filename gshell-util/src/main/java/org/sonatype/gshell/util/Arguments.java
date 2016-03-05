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
package org.sonatype.gshell.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils for command-line arguments.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Arguments
{
    public static Object[] shift(final Object[] args) {
        return shift(args, 1);
    }

    public static Object[] shift(final Object[] source, int pos) {
        assert source != null;
        assert source.length >= pos;

        Object[] target = (Object[]) Array.newInstance(source.getClass().getComponentType(), source.length - pos);

        System.arraycopy(source, pos, target, 0, target.length);

        return target;
    }

    public static String[] toStringArray(final Object[] args) {
        assert args != null;

        String[] strings = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            strings[i] = String.valueOf(args[i]);
        }

        return strings;
    }

    public static String[] clean(final String[] args) {
        assert args != null;

        List<String> cleaned = new ArrayList<String>();

        StringBuilder currentArg = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            boolean addedToBuffer = false;

            if (arg.startsWith("\"")) {
                // if we're in the process of building up another arg, push it and start over.
                // this is for the case: "-Dfoo=bar "-Dfoo2=bar two" (note the first unterminated quote)
                if (currentArg != null) {
                    cleaned.add(currentArg.toString());
                }

                // start building an argument here.
                currentArg = new StringBuilder(arg.substring(1));
                addedToBuffer = true;
            }

            // this has to be a separate "if" statement, to capture the case of: "-Dfoo=bar"
            if (arg.endsWith("\"")) {
                String cleanArgPart = arg.substring(0, arg.length() - 1);

                // if we're building an argument, keep doing so.
                if (currentArg != null) {
                    // if this is the case of "-Dfoo=bar", then we need to adjust the buffer.
                    if (addedToBuffer) {
                        currentArg.setLength(currentArg.length() - 1);
                    }
                    // otherwise, we trim the trailing " and append to the buffer.
                    else {
                        // TODO: introducing a space here...not sure what else to do but collapse whitespace
                        currentArg.append(' ').append(cleanArgPart);
                    }

                    cleaned.add(currentArg.toString());
                }
                else {
                    cleaned.add(cleanArgPart);
                }

                currentArg = null;

                continue;
            }

            // if we haven't added this arg to the buffer, and we ARE building an argument
            // buffer, then append it with a preceding space...again, not sure what else to
            // do other than collapse whitespace.
            // NOTE: The case of a trailing quote is handled by nullifying the arg buffer.
            if (!addedToBuffer) {
                if (currentArg != null) {
                    currentArg.append(' ').append(arg);
                }
                else {
                    cleaned.add(arg);
                }
            }
        }

        if (currentArg != null) {
            cleaned.add(currentArg.toString());
        }

        if (cleaned.isEmpty()) {
            return args;
        }
        else {
            return cleaned.toArray(new String[cleaned.size()]);
        }
    }
}
