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

package org.sonatype.gshell.commands.standard;

import org.junit.Test;
import org.sonatype.gshell.command.CommandTestSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link AliasCommand}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AliasCommandTest
    extends CommandTestSupport
{
    public AliasCommandTest() {
        super(AliasCommand.class);
    }

    @Test
    public void testDefineAlias() throws Exception {
        assertFalse(aliasRegistry.containsAlias("foo"));

        Object result = executeWithArgs("foo bar");
        assertEqualsSuccess(result);

        assertTrue(aliasRegistry.containsAlias("foo"));

        String alias = aliasRegistry.getAlias("foo");
        assertEquals(alias, "bar");
    }

    @Test
    public void testRedefineAlias() throws Exception {
        testDefineAlias();
        assertTrue(aliasRegistry.containsAlias("foo"));
        
        Object result = executeWithArgs("foo baz");
        assertEqualsSuccess(result);

        assertTrue(aliasRegistry.containsAlias("foo"));

        String alias = aliasRegistry.getAlias("foo");
        assertEquals(alias, "baz");
    }

    @Test
    public void testExecuteAlias() throws Exception {
        assertFalse(aliasRegistry.containsAlias("make-alias"));

        Object result = executeWithArgs("make-alias alias");
        assertEqualsSuccess(result);

        assertTrue(aliasRegistry.containsAlias("make-alias"));
        String alias = aliasRegistry.getAlias(("make-alias"));
        assertEquals(alias, "alias");

        result = execute("make-alias foo bar");
        assertEqualsSuccess(result);

        assertTrue(aliasRegistry.containsAlias("foo"));
        alias = aliasRegistry.getAlias("foo");
        assertEquals(alias, "bar");
    }
}