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

package org.apache.maven.shell.testsuite;

import org.apache.maven.shell.Variables;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.junit.Test;

/**
 * Support for testing {@link Command} instances.
 *
 * @version $Rev$ $Date$
 */
public abstract class CommandTestSupport
    extends ShellTestSupport
{
    protected final String name;

    protected AliasRegistry aliasRegistry;

    protected CommandRegistry commandRegistry;

    protected Variables vars;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // For simplicity of output verification disable ANSI
        Ansi.setEnabled(false);
        
        vars = getShell().getVariables();
        aliasRegistry = lookup(AliasRegistry.class);
        commandRegistry = lookup(CommandRegistry.class);
    }

    @Override
    protected void tearDown() throws Exception {
        commandRegistry = null;
        aliasRegistry = null;
        vars = null;

        super.tearDown();
    }

    protected CommandTestSupport(final String name) {
        assertNotNull(name);
        this.name = name;
    }

    protected Object execute() throws Exception {
        return execute(name);
    }

    protected Object executeWithArgs(final String args) throws Exception {
        assertNotNull(args);
        return execute(name + " " + args);
    }

    protected void assertEqualsSuccess(final Object result) {
        assertEquals(Command.Result.SUCCESS, result);
    }

    protected void assertEqualsFailure(final Object result) {
        assertEquals(Command.Result.FAILURE, result);
    }

    @Test
    public void testRegistered() throws Exception {
        assertTrue(commandRegistry.containsCommand(name));
    }

    @Test
    public void testHelp() throws Exception {
        Object result;

        result = executeWithArgs("--help");
        assertEqualsSuccess(result);

        result = executeWithArgs("-h");
        assertEqualsSuccess(result);
    }

    @Test
    public void testDefault() throws Exception {
        Object result = execute();
        assertEqualsSuccess(result);
    }
}