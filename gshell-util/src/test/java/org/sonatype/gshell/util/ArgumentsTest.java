/*
 * Copyright (c) 2009-2013 the original author or authors.
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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Arguments} class.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ArgumentsTest
{
    @Test
    public void testShift() {
        Object[] args = { 1, 2, 3, 4 };

        Object[] shifted = Arguments.shift(args);

        assertEquals(args.length - 1, shifted.length);

        assertEquals(2, shifted[0]);
        assertEquals(3, shifted[1]);
        assertEquals(4, shifted[2]);
    }

    @Test
    public void testShift2() {
        Object[] args = { "a", "b", 1, 2 };

        Object[] shifted = Arguments.shift(args);

        assertEquals(args.length - 1, shifted.length);
    }

    @Test
    public void testShift3() {
        String[] args = { "a" };

        Object[] shifted = Arguments.shift(args);

        assertEquals(args.length - 1, shifted.length);
    }
}