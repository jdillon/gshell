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

package org.sonatype.gshell.util.cli2;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests options.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class OptionsTest
    extends CliProcessorTestSupport
{
    private static class Simple
    {
        @Option(name = "h", longName = "help")
        boolean help;

        @Option(name = "v", optionalArg = true)
        Boolean verbose;

        @Option(name = "s", optionalArg = false)
        String string;

        @Option(name = "S", args=2, optionalArg = false)
        List<String> strings;
    }

    private Simple bean;

    @Override
    protected Object createBean() {
        return bean = new Simple();
    }

    @Test
    public void testHelp() throws Exception {
        clp.process("-h");
        assertTrue(bean.help);
    }

    @Test
    public void testHelp2() throws Exception {
        clp.process("--help");
        assertTrue(bean.help);
    }

    @Test
    public void testVerbose() throws Exception {
        clp.process("-v");
        assertTrue(bean.verbose);
    }

    @Test
    public void testVerbose2() throws Exception {
        clp.process("-v", "false");
        assertFalse(bean.verbose);
    }

    @Test
    public void testString() throws Exception {
        try {
            clp.process("-s");
            fail();
        }
        catch (Exception e) {
            // ignore
        }

        clp.process("-s", "foo");
        assertEquals("foo", bean.string);
    }

    @Test
    public void testStrings() throws Exception {
        try {
            clp.process("-S");
            fail();
        }
        catch (Exception e) {
            // ignore
        }

//        TODO: Add validation to expected/actual arguments
//        try {
//            clp.process("-S foo");
//            fail();
//        }
//        catch (Exception e) {
//            // ignore
//        }

        clp.process("-S", "foo", "bar");
        assertEquals("foo", bean.strings.get(0));
        assertEquals("bar", bean.strings.get(1));
    }

    @Test
    public void testStrings2() throws Exception {
        try {
            clp.process("-S", "foo", "bar", "baz");
            fail();
        }
        catch (Exception e) {
            // ignore
        }
    }
}