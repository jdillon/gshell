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

package org.apache.gshell.testsuite.basic;

import org.apache.gshell.cli.ProcessingException;
import org.apache.gshell.testsuite.CommandTestSupport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Tests for the {@link SourceCommand}.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SourceCommandTest
    extends CommandTestSupport
{
    public SourceCommandTest() {
        super("source");
    }
    
    @Override
    @Test
    public void testDefault() throws Exception {
        try {
            super.testDefault();
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }

    @Test
    public void testDependenciesRegistered() throws Exception {
        assertTrue(commandRegistry.containsCommand("set"));
    }

    @Test
    public void testTooManyArguments() throws Exception {
        try {
            executeWithArgs("1 2");
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }

    @Test
    public void testNoSuchFile() throws Exception {
        try {
            executeWithArgs("no-such-file");
            fail();
        }
        catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void test1() throws Exception {
        URL script = getClass().getResource("test1.tsh");
        assertNotNull(script);
        Object result = executeWithArgs(script.toExternalForm());
        assertEqualsSuccess(result);
    }

    @Test
    public void test2() throws Exception {
        assertFalse(vars.contains("foo"));

        URL script = getClass().getResource("test2.tsh");
        assertNotNull(script);
        Object result = executeWithArgs(script.toExternalForm());
        assertEqualsSuccess(result);

        assertTrue(vars.contains("foo"));
        Object value = vars.get("foo");
        assertEquals(value, "bar");
    }
}