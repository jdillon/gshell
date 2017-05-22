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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandRegistry;
import com.planet57.gshell.command.CommandRegistryImpl;
import com.planet57.gshell.functions.internal.FunctionRegistryImpl;
import com.planet57.gshell.help.HelpPageManagerImpl;
import com.planet57.gshell.internal.BeanContainer;
import com.planet57.gshell.logging.logback.TargetConsoleAppender;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellBuilder;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.apache.felix.gogo.runtime.threadio.ThreadIOImpl;
import org.apache.felix.service.threadio.ThreadIO;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sonatype.goodies.testsupport.TestTracer;
import org.sonatype.goodies.testsupport.TestUtil;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
    Ansi.setEnabled(false);
  }

  protected final TestUtil util = new TestUtil(getClass());

  protected final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * The name of the command under-test.
   */
  private final String name;

  @Rule
  public final TestTracer tracer = new TestTracer(this);

  private BeanContainer container;

  private Injector injector;

  private Terminal terminal;

  private BufferIO io;

  private Shell shell;

  private CommandRegistryImpl commandRegistry;

  private Variables variables;

  private Branding branding;

  protected final Map<String, Class> requiredCommands = new HashMap<>();

  private final ThreadIOImpl threadIO = new ThreadIOImpl();

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
    terminal = TerminalBuilder.builder().dumb(true).build();
    io = new BufferIO(terminal);
    variables = new VariablesSupport();
    branding = new TestBranding(util.resolveFile("target/shell-home"));

    container = new BeanContainer();
    List<Module> modules = new ArrayList<>();
    modules.add(binder -> {
      binder.bind(BeanContainer.class).toInstance(container);
      binder.bind(ThreadIO.class).toInstance(threadIO);
      binder.bind(Branding.class).toInstance(branding);
    });
    configureModules(modules);
    modules.add(createSpaceModule());

    injector = Guice.createInjector(Stage.DEVELOPMENT, new WireModule(modules));
    container.add(injector, 0);

    shell = injector.getInstance(ShellBuilder.class)
      .branding(branding)
      .io(io)
      .variables(variables)
      .build();

    variables = shell.getVariables();
    commandRegistry = injector.getInstance(CommandRegistryImpl.class);
    commandRegistry.setDiscoveryEnabled(false);

    // disable function discovery by default
    FunctionRegistryImpl functionRegistry = injector.getInstance(FunctionRegistryImpl.class);
    functionRegistry.setDiscoveryEnabled(false);

    // disable default help-page discovery
    HelpPageManagerImpl helpPageManager = injector.getInstance(HelpPageManagerImpl.class);
    helpPageManager.setDiscoveryEnabled(false);

    // force logging to resolve to specific stream and not re-resolve System.out
    TargetConsoleAppender.setTarget(System.out);

    threadIO.start();
    shell.start();

    // register required commands
    for (Map.Entry<String, Class> entry : requiredCommands.entrySet()) {
      commandRegistry.registerCommand(entry.getKey(), entry.getValue());
    }

    // allow test to become aware of injection
    injector.injectMembers(this);
  }

  /**
   * Extension-point for sub-class to configure any additional modules for test environment.
   */
  protected void configureModules(@Nonnull final List<Module> modules) {
    // empty
  }

  /**
   * Expose ability to adjust the {@link SpaceModule}; for most cases this should be left ASIS.
   */
  protected SpaceModule createSpaceModule() {
    URLClassSpace space = new URLClassSpace(getClass().getClassLoader());
    return new SpaceModule(space, BeanScanning.INDEX);
  }

  @After
  public void tearDown() throws Exception {
    threadIO.stop();

    if (shell != null) {
      shell.stop();
      shell = null;
    }

    if (terminal != null) {
      terminal.close();
      terminal = null;
    }

    if (container != null) {
      container.clear();
      container = null;
    }

    commandRegistry = null;
    variables = null;
    io = null;
    injector = null;
  }

  protected Shell getShell() {
    checkState(shell != null);
    return shell;
  }

  protected BufferIO getIo() {
    checkState(io != null);
    return io;
  }

  protected <T> T lookup(final Class<T> type) {
    checkState(injector != null);
    return injector.getInstance(type);
  }

  /**
   * Execute a raw shell line.
   */
  protected Object executeLine(final String line) throws Exception {
    checkNotNull(line);
    try {
      return getShell().execute(line);
    }
    finally {
      io.dump(log);
    }
  }

  /**
   * Execute command registered for the test.
   */
  protected Object executeCommand(final String... args) throws Exception {
    checkNotNull(args);

    try {
      return getShell().execute(name + " " + String.join(" ", args));
    }
    finally {
      io.dump(log);
    }
  }

  //
  // Default tests for all commands
  //

  /**
   * All commands should be registered with {@link CommandRegistry} component.
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

    result = executeCommand("--help");
    assertThat(result, nullValue());

    result = executeCommand("-h");
    assertThat(result, nullValue());
  }
}
