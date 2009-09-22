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

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.VariableNames;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.ansi.Ansi;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.core.ShellBuilder;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.apache.maven.shell.testsupport.PlexusTestSupport;
import org.apache.maven.shell.testsupport.TestIO;
import org.apache.maven.shell.testsupport.TestUtil;
import org.codehaus.plexus.util.StringUtils;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Support for testing {@link Command} instances.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CommandTestSupport
    implements VariableNames
{
    protected final String name;

    private final TestUtil util = new TestUtil(this);

    private PlexusTestSupport plexus;
    
    private TestIO io;

    private Shell shell;

    protected AliasRegistry aliasRegistry;

    protected CommandRegistry commandRegistry;

    protected Variables vars;

    protected CommandTestSupport(final String name) {
        assertNotNull(name);
        this.name = name;
    }

    @Before
    public void setUp() throws Exception {
        plexus = new PlexusTestSupport(this);

        io = new TestIO();
        
        shell = new ShellBuilder()
                .setContainer(plexus.getContainer())
                .setBranding(new TestBranding(util.resolveFile("target/shell-home")))
                .setIo(io)
                .create();

        // For simplicity of output verification disable ANSI
        Ansi.setEnabled(false);
        
        vars = shell.getVariables();
        aliasRegistry = plexus.lookup(AliasRegistry.class);
        commandRegistry = plexus.lookup(CommandRegistry.class);
    }

    @After
    public void tearDown() throws Exception {
        commandRegistry = null;
        aliasRegistry = null;
        vars = null;
        io = null;
        shell.close();
        shell = null;
        plexus = null;
    }

    protected Shell getShell() {
        assertNotNull(shell);
        return shell;
    }

    protected TestIO getIo() {
        assertNotNull(io);
        return io;
    }

    protected Object execute(final String line) throws Exception {
        assertNotNull(line);
        return getShell().execute(line);
    }

    protected Object execute() throws Exception {
        return execute(name);
    }

    protected Object execute(final String... args) throws Exception {
        return execute(StringUtils.join(args, " "));
    }

    protected Object executeWithArgs(final String args) throws Exception {
        assertNotNull(args);
        return execute(name, args);
    }

    protected Object executeWithArgs(final String... args) throws Exception {
        assertNotNull(args);
        return execute(name, StringUtils.join(args, " "));
    }

    //
    // Assertion helpers
    //
    
    protected void assertEqualsSuccess(final Object result) {
        assertEquals(Command.Result.SUCCESS, result);
    }

    protected void assertEqualsFailure(final Object result) {
        assertEquals(Command.Result.FAILURE, result);
    }

    protected void assertOutputEquals(final String expected) {
        assertEquals(getIo().getOutputString(), expected);
    }

    protected void assertErrorOutputEquals(final String expected) {
        assertEquals(getIo().getErrorString(), expected);
    }

    //
    // Some default tests for all commands
    //

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