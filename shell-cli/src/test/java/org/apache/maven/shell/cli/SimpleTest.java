/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.cli;

import junit.framework.TestCase;

/**
 * Some simple tests to validate basic functionality.
 *
 * @version $Rev$ $Date$
 */
public class SimpleTest
    extends TestCase
{
    Simple bean;

    CommandLineProcessor clp;

    protected void setUp() throws Exception {
        bean = new Simple();
        clp = new CommandLineProcessor(bean);

        assertEquals(2, clp.getOptionHandlers().size());
        assertEquals(1, clp.getArgumentHandlers().size());
    }

    protected void tearDown() throws Exception {
        bean = null;
        clp = null;
    }

    public void testSimple0() throws Exception {
        clp.process("-v");

        assertFalse(bean.help);
    }

    public void testSimple1() throws Exception {
        clp.process("--help");

        assertTrue(bean.help);
    }

    public void testSimple2() throws Exception {
        clp.process("-h");

        assertTrue(bean.help);
    }

    public void testSimple3() throws Exception {
        try {
            clp.process("-f");
            fail();
        }
        catch (ProcessingException ignore) {}

        assertFalse(bean.help);
    }

    public void testSimple4() throws Exception {
       clp.process("-h");

       assertFalse(bean.verbose);
    }

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
