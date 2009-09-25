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
import org.apache.gshell.core.commands.UnaliasCommand;
import org.apache.gshell.testsuite.CommandTestSupport;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for the {@link UnaliasCommand}.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class UnaliasCommandTest
    extends CommandTestSupport
{
    public UnaliasCommandTest() {
        super("unalias", UnaliasCommand.class);
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
    public void testUndefineAlias() throws Exception {
        assertFalse(aliasRegistry.containsAlias("foo"));
        aliasRegistry.registerAlias("foo", "bar");
        
        Object result = executeWithArgs("foo");
        assertEqualsSuccess(result);
        assertFalse(aliasRegistry.containsAlias("foo"));
    }
}