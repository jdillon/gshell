/**
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
package org.sonatype.gshell.command.support;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.branding.TestBranding;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.guice.CoreModule;
import org.sonatype.gshell.logging.LoggingSystem;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellErrorHandler;
import org.sonatype.gshell.shell.ShellImpl;
import org.sonatype.gshell.shell.ShellPrompt;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.variables.Variables;
import org.sonatype.gshell.variables.VariablesImpl;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.locators.DefaultBeanLocator;
import org.sonatype.guice.bean.locators.MutableBeanLocator;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.name.Names.named;
import static org.junit.Assert.*;

/**
 * Support for testing {@link org.sonatype.gshell.command.CommandAction} instances.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public abstract class CommandTestSupport
{
    protected final String name;

    private final TestUtil util = new TestUtil(this);

    private DefaultBeanLocator container;

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
        container = new DefaultBeanLocator();
        io = new TestIO();
        vars = new VariablesImpl();

        Module boot = new AbstractModule()
        {
            @Override
            protected void configure() {
                bind(MutableBeanLocator.class).toInstance(container);
                bind(LoggingSystem.class).to(TestLoggingSystem.class);
                bind(ConsolePrompt.class).to(ShellPrompt.class);
                bind(ConsoleErrorHandler.class).to(ShellErrorHandler.class);
                bind(Branding.class).toInstance(new TestBranding(util.resolveFile("target/shell-home")));
                bind(IO.class).annotatedWith(named("main")).toInstance(io);
                bind(Variables.class).annotatedWith(named("main")).toInstance(vars);
            }
        };

        List<Module> modules = new ArrayList<Module>();
        modules.add(boot);
        configureModules(modules);

        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new WireModule(modules));
        container.add(injector, 0);

        CommandRegistrar registrar = injector.getInstance(CommandRegistrar.class);
        for (Map.Entry<String,Class> entry : requiredCommands.entrySet()) {
            registrar.registerCommand(entry.getKey(), entry.getValue().getName());
        }

        shell = injector.getInstance(ShellImpl.class);

        // For simplicity of output verification disable ANSI
        Ansi.setEnabled(false);
        
        vars = shell.getVariables();

        aliasRegistry = injector.getInstance(AliasRegistry.class);
        commandRegistry = injector.getInstance(CommandRegistry.class);
    }

    protected void configureModules(final List<Module> modules) {
        assert modules != null;
        modules.add(createSpaceModule());
        modules.add(new CoreModule());
    }

    protected SpaceModule createSpaceModule() {
        URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
        return new SpaceModule(space, BeanScanning.INDEX);
    }

    @After
    public void tearDown() throws Exception {
        commandRegistry = null;
        aliasRegistry = null;
        vars = null;
        io = null;
        if (shell != null) {
            shell.close();
        }
        shell = null;
        requiredCommands.clear();
        if (container != null) {
            container.clear();
            container = null;
        }
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
        Assert.assertEquals(CommandAction.Result.SUCCESS, result);
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