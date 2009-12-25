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

package org.sonatype.gshell.io;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for the {@link PrefixingStream} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class PrefixingStreamTest
{
    @Test
    public void testStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrefixingStream prefixed = new PrefixingStream("TEST", baos);
        PrintStream out = new PrintStream(prefixed);

        out.println("HI");
        out.println("there");
        out.print("foo");
        out.print("bar");
        out.println("baz");
        out.flush();

        String tmp = new String(baos.toByteArray());
        System.out.println(tmp);

        String sep = System.getProperty("line.separator");
        String expected = "TESTHI" + sep +
                "TESTthere" + sep +
                "TESTfoobarbaz" + sep;

        assertEquals(expected, tmp);
    }
}