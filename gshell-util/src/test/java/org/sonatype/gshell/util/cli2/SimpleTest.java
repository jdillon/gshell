/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.util.cli2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Some simple tests to validate basic functionality.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SimpleTest
    extends CliProcessorTestSupport
{
    private static class Simple
    {
        @Option(name = "h", longName = "help")
        boolean help;

        @Option(name = "v", longName = "verbose")
        Boolean verbose;

        @Argument
        String arg1;
    }

    private Simple bean;

    @Override
    protected Object createBean() {
        bean = new Simple();
        return bean;
    }

    @Test
    public void testName() throws Exception {
        clp.process("-v");

        assertFalse(bean.help);
        assertTrue(bean.verbose);
    }

    @Test
    public void testName2() throws Exception {
        clp.process("-h");

        assertTrue(bean.help);
        assertNull(bean.verbose);
    }

    @Test
    public void testLongName() throws Exception {
        clp.process("--help");

        assertTrue(bean.help);
        assertNull(bean.verbose);
    }

    @Test
    public void testInvalidOption() throws Exception {
        try {
            clp.process("-f");
            fail();
        }
        catch (Exception e) {
            // ignore
        }

        assertFalse(bean.help);
        assertNull(bean.verbose);
    }

    @Test
    public void testArg() throws Exception {
        clp.process("foo");

        assertEquals(bean.arg1, "foo");
        assertFalse(bean.help);
        assertNull(bean.verbose);
    }

    @Test
    public void testTooManyArgs() throws Exception {
        try {
            clp.process("foo", "bar", "baz");
            fail();
        }
        catch (Exception e) {
            // ignore
        }
    }
}