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
package org.sonatype.gshell.commands.standard;

import org.junit.Test;
import org.sonatype.gshell.command.support.CommandTestSupport;
import org.sonatype.gshell.shell.History;
import org.sonatype.gshell.util.converter.ConversionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link RecallHistoryCommand}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RecallHistoryCommandTest
    extends CommandTestSupport
{
    public RecallHistoryCommandTest() {
        super(RecallHistoryCommand.class);
    }

    @Override
    public void setUp() throws Exception {
        requiredCommands.put("set", SetCommand.class);
        super.setUp();
    }

    @Override
    @Test
    public void testDefault() throws Exception {
        try {
            super.testDefault();
            fail();
        }
        catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testTooManyArguments() throws Exception {
        try {
            executeWithArgs("1 2");
            fail();
        }
        catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testIndexOutOfRange() throws Exception {
        Object result = executeWithArgs(String.valueOf(Integer.MAX_VALUE));
        assertEqualsFailure(result);
    }

    @Test
    public void testInvalidIndex() throws Exception {
        try {
            executeWithArgs("foo");
            fail();
        }
        catch (ConversionException e) {
            // expected
        }
    }

    @Test
    public void testRecallElement() throws Exception {
        History history = getShell().getHistory();

        // Clear history and make sure there is no foo variable
        history.clear();
        assertFalse(vars.contains("foo"));

        // Then add 2 items, both setting foo
        history.add("set foo bar");
        history.add("set foo baz");

        assertEquals(2, getShell().getHistory().size());

        // Recall the first, which sets foo to bar
        Object result = executeWithArgs("1");
        assertEqualsSuccess(result);

        // Make sure it executed
        assertEquals("bar", vars.get("foo"));
    }
}