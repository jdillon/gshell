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

package org.sonatype.gshell.util.cli;

import org.junit.Test;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.util.cli.Option;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Some simple tests to validate basic functionality.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SimpleTest
    extends CommandLineProcessorTestSupport
{
    private Simple bean;

    @Override
    protected Object createBean() {
        bean = new Simple();
        return bean;
    }

    @Test
    public void testOptionsArgumentsSize() {
        assertOptionsArgumentsSize(2, 1);
    }

    @Test
    public void testSimple0() throws Exception {
        clp.process("-v");

        assertFalse(bean.help);
    }

    @Test
    public void testSimple1() throws Exception {
        clp.process("--help");

        assertTrue(bean.help);
    }

    @Test
    public void testSimple2() throws Exception {
        clp.process("-h");

        assertTrue(bean.help);
    }

    @Test
    public void testSimple3() throws Exception {
        try {
            clp.process("-f");
            fail();
        }
        catch (Exception ignore) {}

        assertFalse(bean.help);
    }

    @Test
    public void testSimple4() throws Exception {
       clp.process("-h");

       assertFalse(bean.verbose);
    }

    @Test
    public void testSimple5() throws Exception {
       clp.process("foo");

       assertEquals(bean.arg1, "foo");
    }

    private static class Simple
    {
        @Option(name="-h", aliases={"--help"})
        boolean help;

        @Option(name="-v", aliases={"--verbose"})
        boolean verbose;

        @Argument
        String arg1;
    }
}
