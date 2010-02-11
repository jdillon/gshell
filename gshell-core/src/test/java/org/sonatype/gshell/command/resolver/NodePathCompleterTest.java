/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.command.resolver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.gshell.command.DummyAction;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRegistryImpl;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.event.EventManagerImpl;
import org.sonatype.gshell.vars.Variables;
import org.sonatype.gshell.vars.VariablesImpl;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.sonatype.gshell.vars.VariableNames.*;

/**
 * Tests for {@link NodePathCompleter}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodePathCompleterTest
{
    private Variables variables;

    private CommandResolver resolver;

    private Node root;

    private NodePathCompleter completer;

    @Before
    public void setUp() throws Exception {
        variables = new VariablesImpl();
        variables.set(SHELL_GROUP, "/");
        variables.set(SHELL_GROUP_PATH, ".:/");

        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule() {
            @Override
            protected void configure() {
                bind(EventManager.class).to(EventManagerImpl.class);
                bind(CommandRegistry.class).to(CommandRegistryImpl.class);
                bind(CommandResolver.class).to(CommandResolverImpl.class);
            }

            @Provides
            private Variables provideVariables() {
                return variables;
            }
        });

        resolver = injector.getInstance(CommandResolver.class);
        completer = injector.getInstance(NodePathCompleter.class);

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
        completer = null;
    }

    protected void assertCompletes(final String input, final String... expected) {
        System.out.println(">");
        try {
            List<CharSequence> candidates = new ArrayList<CharSequence>();
            int result = completer.complete(input, 0, candidates);

            System.out.println("Result: " + result + ", Candidates: " + candidates);
            
            assertEquals(expected.length, candidates.size());
            for (int i=0; i<expected.length;i++) {
                assertEquals(expected[i], candidates.get(i));
            }
        }
        finally {
            System.out.println("<");
            System.out.flush();
        }
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
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".");
        assertCompletes("a1", "a1 ");
    }

    @Test
    public void test4b() {
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".");
        assertCompletes("", "..", "a1", "a2", "b1");
    }

    @Test
    public void test4c() {
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".:/");
        assertCompletes("", "..", "a1", "a2", "b1", "foo/", "bar", "baz");
    }

    @Test
    public void test4d() {
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".");
        assertCompletes("a", "a1", "a2");
    }

    @Test
    public void test4e() {
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".:/");
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
        variables.set(SHELL_GROUP, "/foo");
        variables.set(SHELL_GROUP_PATH, ".:/");
        assertCompletes(".", "./", "../");
    }
}