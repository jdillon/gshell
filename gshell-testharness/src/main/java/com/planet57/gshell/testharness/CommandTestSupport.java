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
import com.planet57.gshell.parser.impl.eval.Evaluator;
import com.planet57.gshell.parser.impl.eval.RegexEvaluator;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sonatype.goodies.lifecycle.Lifecycles;
import org.sonatype.goodies.testsupport.TestTracer;
import org.sonatype.goodies.testsupport.TestUtil;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.inject.name.Names.named;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

// FIXME: Goodies TestSupport initMocks() is causing some issues in mvnsh; so don't use it for now

/**
 * Support for testing {@link CommandAction} instances.
 *
 * @since 3.0
 */
public abstract class CommandTestSupport
{
  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  protected final TestUtil util = new TestUtil(getClass());

  protected final String name;

  @Rule
  public final TestTracer tracer = new TestTracer(this);

  private BeanContainer container;

  private TestIO io;

  private Shell shell;

  protected AliasRegistry aliasRegistry;

  protected CommandRegistry commandRegistry;

  protected Variables vars;

  protected final Map<String, Class> requiredCommands = new HashMap<>();

  protected CommandTestSupport(final String name, final Class<?> type) {
    this.name = checkNotNull(name);
    checkNotNull(type);
    requiredCommands.put(name, type);
  }

  protected CommandTestSupport(final Class<?> type) {
    this(type.getAnnotation(Command.class).name(), type);
  }

  @Before
  public void setUp() throws Exception {
    // For simplicity of output verification disable ANSI
    Ansi.setEnabled(false);

    container = new BeanContainer();
    io = new TestIO();
    vars = new VariablesSupport();

    List<Module> modules = new ArrayList<>();
    modules.add(binder -> {
      binder.bind(BeanContainer.class).toInstance(container);
      binder.bind(LoggingSystem.class).to(TestLoggingSystem.class);
      binder.bind(Branding.class).toInstance(new TestBranding(util.resolveFile("target/shell-home")));
      binder.bind(IO.class).annotatedWith(named("main")).toInstance(io);
      binder.bind(Variables.class).annotatedWith(named("main")).toInstance(vars);
      binder.bind(Evaluator.class).to(RegexEvaluator.class);
    });
    configureModules(modules);

    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new WireModule(modules));
    container.add(injector, 0);

    Lifecycles.start(injector.getInstance(EventManager.class));

    CommandRegistrar registrar = injector.getInstance(CommandRegistrar.class);
    for (Map.Entry<String, Class> entry : requiredCommands.entrySet()) {
      registrar.registerCommand(entry.getKey(), entry.getValue().getName());
    }

    shell = injector.getInstance(ShellImpl.class);
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
    checkState(shell != null);
    return shell;
  }

  protected TestIO getIo() {
    checkState(io != null);
    return io;
  }

  protected Object execute(final String line) throws Exception {
    checkNotNull(line);
    return getShell().execute(line);
  }

  protected Object execute() throws Exception {
    return execute(name);
  }

  protected Object execute(final String... args) throws Exception {
    return execute(Joiner.on(" ").join(args));
  }

  protected Object executeWithArgs(final String args) throws Exception {
    checkNotNull(args);
    return execute(name, args);
  }

  protected Object executeWithArgs(final String... args) throws Exception {
    checkNotNull(args);
    return execute(name, Joiner.on(" ").join(args));
  }

  //
  // Assertion helpers
  //

  protected void assertEqualsSuccess(final Object result) {
    assertThat(result, is(CommandAction.Result.SUCCESS));
  }

  protected void assertEqualsFailure(final Object result) {
    assertThat(result, is(CommandAction.Result.FAILURE));
  }

  protected void assertOutputEquals(final String expected) {
    assertThat(getIo().getOutputString(), is(expected));
  }

  protected void assertErrorOutputEquals(final String expected) {
    assertThat(getIo().getErrorString(), is(expected));
  }

  //
  // Default tests for all commands
  //

  /**
   * All commands should end registered with {@link CommandRegistry} component.
   */
  @Test
  public void testRegistered() throws Exception {
    assertThat(commandRegistry.containsCommand(name), is(true));
  }

  /**
   * All commands must provide {@code --help} and {@code -h} handling.
   */
  @Test
  public void testHelp() throws Exception {
    Object result;

    result = executeWithArgs("--help");
    assertEqualsSuccess(result);

    result = executeWithArgs("-h");
    assertEqualsSuccess(result);
  }
}
