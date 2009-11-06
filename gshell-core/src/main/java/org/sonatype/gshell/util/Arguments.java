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

package org.sonatype.gshell.util;

import java.lang.reflect.Array;

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
}
