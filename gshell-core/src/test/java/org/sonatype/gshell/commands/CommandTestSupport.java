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

package org.sonatype.gshell.commands;

import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.gshell.Shell;
import org.sonatype.gshell.TestBranding;
import org.sonatype.gshell.TestShellBuilder;
import org.sonatype.gshell.VariableNames;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.testsupport.TestIO;
import org.sonatype.gshell.testsupport.TestUtil;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.ansi.Ansi;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Support for testing {@link org.sonatype.gshell.command.CommandAction} instances.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CommandTestSupport
    implements VariableNames
{
    protected final String name;

    protected final TestShellBuilder builder = new TestShellBuilder();;

    private final TestUtil util = new TestUtil(this);

    private TestIO io;

    private Shell shell;

    protected AliasRegistry aliasRegistry;

    protected CommandRegistry commandRegistry;

    protected Variables vars;

    protected final Map<String,Class> requiredCommands = new HashMap<String,Class>();

    protected CommandTestSupport(final String name, final Class<?> type) {
        assertNotNull(name);
        assertNotNull(type);
        this.name = name;
        requiredCommands.put(name, type);
    }

    protected CommandTestSupport(final Class<?> type) {
        this(type.getAnnotation(Command.class).name(), type);
    }

    @Before
    public void setUp() throws Exception {
        io = new TestIO();

        TestShellBuilder builder = new TestShellBuilder();
        Injector injector = builder.getInjector();

        shell = builder
                .setBranding(new TestBranding(util.resolveFile("target/shell-home")))
                .setIo(io)
                .setRegisterCommands(false)
                .create();

        CommandRegistrar registrar = injector.getInstance(CommandRegistrar.class);
        for (Map.Entry<String,Class> entry : requiredCommands.entrySet()) {
            registrar.registerCommand(entry.getKey(), entry.getValue().getName());
        }

        // For simplicity of output verification disable ANSI
        Ansi.setEnabled(false);
        
        vars = shell.getVariables();

        aliasRegistry = injector.getInstance(AliasRegistry.class);
        commandRegistry = injector.getInstance(CommandRegistry.class);
    }

    @After
    public void tearDown() throws Exception {
        commandRegistry = null;
        aliasRegistry = null;
        vars = null;
        io = null;
        shell.close();
        shell = null;
        requiredCommands.clear();
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
        return execute(Strings.join(args, " "));
    }

    protected Object executeWithArgs(final String args) throws Exception {
        assertNotNull(args);
        return execute(name, args);
    }

    protected Object executeWithArgs(final String... args) throws Exception {
        assertNotNull(args);
        return execute(name, Strings.join(args, " "));
    }

    //
    // Assertion helpers
    //
    
    protected void assertEqualsSuccess(final Object result) {
        assertEquals(CommandAction.Result.SUCCESS, result);
    }

    protected void assertEqualsFailure(final Object result) {
        assertEquals(CommandAction.Result.FAILURE, result);
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