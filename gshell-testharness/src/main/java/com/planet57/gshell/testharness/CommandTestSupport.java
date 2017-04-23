/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell.testharness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.CommandRegistrar;
import com.planet57.gshell.command.registry.CommandRegistry;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.guice.BeanContainer;
import com.planet57.gshell.logging.LoggingSystem;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellImpl;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.lifecycle.Lifecycles;
import org.sonatype.goodies.testsupport.TestSupport;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.named;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Support for testing {@link CommandAction} instances.
 *
 * @since 3.0
 */
public abstract class CommandTestSupport
  extends TestSupport
{
  protected final String name;

  private BeanContainer container;

  private TestIO io;

  private Shell shell;

  protected AliasRegistry aliasRegistry;

  protected CommandRegistry commandRegistry;

  protected Variables vars;

  protected final Map<String, Class> requiredCommands = new HashMap<>();

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
    container = new BeanContainer();
    io = new TestIO();
    vars = new VariablesSupport();

    Module boot = new AbstractModule()
    {
      @Override
      protected void configure() {
        bind(BeanContainer.class).toInstance(container);
        bind(LoggingSystem.class).to(TestLoggingSystem.class);
        bind(Branding.class).toInstance(new TestBranding(util.resolveFile("target/shell-home")));
        bind(IO.class).annotatedWith(named("main")).toInstance(io);
        bind(Variables.class).annotatedWith(named("main")).toInstance(vars);
      }
    };

    List<Module> modules = new ArrayList<>();
    modules.add(boot);
    configureModules(modules);

    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new WireModule(modules));
    container.add(injector, 0);

    Lifecycles.start(injector.getInstance(EventManager.class));

    CommandRegistrar registrar = injector.getInstance(CommandRegistrar.class);
    for (Map.Entry<String, Class> entry : requiredCommands.entrySet()) {
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
    checkNotNull(modules);
    modules.add(createSpaceModule());
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
    return execute(Joiner.on(" ").join(args));
  }

  protected Object executeWithArgs(final String args) throws Exception {
    assertNotNull(args);
    return execute(name, args);
  }

  protected Object executeWithArgs(final String... args) throws Exception {
    assertNotNull(args);
    return execute(name, Joiner.on(" ").join(args));
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
    Assert.assertEquals(getIo().getOutputString(), expected);
  }

  protected void assertErrorOutputEquals(final String expected) {
    Assert.assertEquals(getIo().getErrorString(), expected);
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
