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
package com.planet57.gshell.command.resolver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.command.registry.CommandRegistry;
import com.planet57.gshell.command.registry.CommandRegistryImpl;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.event.EventManagerImpl;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.variables.VariablesSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

/**
 * Tests for {@link NodePathCompleter}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodePathCompleterTest
  extends TestSupport
{
  private Variables variables;

  private CommandResolver resolver;

  private Node root;

  private NodePathCompleter underTest;

  @Before
  public void setUp() throws Exception {
    variables = new VariablesSupport();
    variables.set(VariableNames.SHELL_GROUP, "/");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".:/");

    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, (Module) binder -> {
      binder.bind(EventManager.class).to(EventManagerImpl.class);
      binder.bind(CommandRegistry.class).to(CommandRegistryImpl.class);
      binder.bind(CommandResolver.class).to(CommandResolverImpl.class);
      binder.bind(Variables.class).toInstance(variables);
    });

    resolver = injector.getInstance(CommandResolver.class);
    underTest = injector.getInstance(NodePathCompleter.class);

    root = resolver.root();
    root.add("foo/a1", new DummyAction());
    root.add("foo/a2", new DummyAction());
    root.add("foo/b1", new DummyAction());
    root.add("bar", new DummyAction());
    root.add("baz", new DummyAction());
  }

  @After
  public void tearDown() {
    resolver = null;
    variables = null;
    root = null;
    underTest = null;
  }

  protected void assertCompletes(final String input, final String... expected) {
    // FIXME: Needs to be adjusted once NodePathCompleter has been re-implemented
//    System.out.println(">");
//    try {
//      List<CharSequence> candidates = new ArrayList<CharSequence>();
//      int result = underTest.complete(input, 0, candidates);
//
//      System.out.println("Result: " + result + ", Candidates: " + candidates);
//
//      assertEquals(expected.length, candidates.size());
//      for (int i = 0; i < expected.length; i++) {
//        assertEquals(expected[i], candidates.get(i));
//      }
//    }
//    finally {
//      System.out.println("<");
//      System.out.flush();
//    }
  }

  @Test
  public void test1a() {
    assertCompletes("b", "bar", "baz");
  }

  @Test
  public void test1b() {
    assertCompletes("bar", "bar ");
  }

  @Test
  public void test1c() {
    assertCompletes("../bar", "../bar ");
  }

  @Test
  public void test2a() {
    assertCompletes("f", "foo/");
  }

  @Test
  public void test2b() {
    assertCompletes("foo/a", "foo/a1", "foo/a2");
  }

  @Test
  public void test2c() {
    assertCompletes("foo", "foo/");
  }

  @Test
  public void test2d() {
    assertCompletes("foo/", "foo/a1", "foo/a2", "foo/b1");
  }

  @Test
  public void test3a() {
    assertCompletes("", "foo/", "bar", "baz");
  }

  @Test
  public void test3b() {
    assertCompletes("/", "/foo/", "/bar", "/baz");
  }

  @Test
  public void test3c() {
    assertCompletes("/foo/", "/foo/a1", "/foo/a2", "/foo/b1");
  }

  @Test
  public void test3d() {
    assertCompletes("/foo/a", "/foo/a1", "/foo/a2");
  }

  @Test
  public void test3e() {
    assertCompletes("./foo/", "./foo/a1", "./foo/a2", "./foo/b1");
  }

  @Test
  public void test3f() {
    assertCompletes("./foo/a", "./foo/a1", "./foo/a2");
  }

  @Test
  public void test4a() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".");
    assertCompletes("a1", "a1 ");
  }

  @Test
  public void test4b() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".");
    assertCompletes("", "..", "a1", "a2", "b1");
  }

  @Test
  public void test4c() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".:/");
    assertCompletes("", "..", "a1", "a2", "b1", "foo/", "bar", "baz");
  }

  @Test
  public void test4d() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".");
    assertCompletes("a", "a1", "a2");
  }

  @Test
  public void test4e() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".:/");
    assertCompletes("a", "a1", "a2");
  }

  @Test
  public void test5a() {
    assertCompletes(".", "./", "../");
  }

  @Test
  public void test5b() {
    assertCompletes("..", "../");
  }

  @Test
  public void test5c() {
    variables.set(VariableNames.SHELL_GROUP, "/foo");
    variables.set(VariableNames.SHELL_GROUP_PATH, ".:/");
    assertCompletes(".", "./", "../");
  }
}
