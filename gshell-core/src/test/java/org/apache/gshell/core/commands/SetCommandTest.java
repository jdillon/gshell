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

package org.apache.gshell.core.commands;

import org.apache.gshell.core.commands.SetCommand;
import org.apache.gshell.core.commands.CommandTestSupport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for the {@link SetCommand}.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SetCommandTest
    extends CommandTestSupport
{
    public SetCommandTest() {
        super("set", SetCommand.class);
    }

    @Test
    public void testDefineVariable() throws Exception {
        assertFalse(vars.contains("foo"));
        Object result = executeWithArgs("foo bar");
        assertEqualsSuccess(result);

        assertTrue(vars.contains("foo"));
        Object value = vars.get("foo");
        assertEquals(value, "bar");
    }

    @Test
    public void testRedefineVariable() throws Exception {
        testDefineVariable();
        assertTrue(vars.contains("foo"));

        Object result = executeWithArgs("foo baz");
        assertEqualsSuccess(result);

        assertTrue(vars.contains("foo"));
        Object value = vars.get("foo");
        assertEquals(value, "baz");
    }

    @Test
    public void testDefineVariableWithExpression() throws Exception {
        assertFalse(vars.contains("foo"));
        Object result = executeWithArgs("foo ${shell.home}");
        assertEqualsSuccess(result);

        assertTrue(vars.contains("foo"));
        Object value = vars.get("foo");
        assertEquals(value, vars.get("shell.home", String.class));
    }
}