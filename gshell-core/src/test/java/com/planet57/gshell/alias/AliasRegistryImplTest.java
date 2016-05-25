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
package com.planet57.gshell.alias;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.event.EventManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link AliasRegistryImpl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AliasRegistryImplTest
{
  private AliasRegistry registry;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule()
    {
      @Override
      protected void configure() {
        bind(EventManager.class).to(EventManagerImpl.class);
        bind(AliasRegistry.class).to(AliasRegistryImpl.class);
      }
    });
    registry = injector.getInstance(AliasRegistry.class);
  }

  @After
  public void tearDown() {
    registry = null;
  }

  @Test
  public void testRegisterAliasInvalid() throws Exception {
    try {
      registry.registerAlias(null, null);
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }

    try {
      registry.registerAlias("foo", null);
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }

    try {
      registry.registerAlias(null, "foo");
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }
  }

  @Test
  public void testRemoveAliasInvalid() throws Exception {
    try {
      registry.removeAlias(null);
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }
  }

  @Test
  public void testGetAliasInvalid() throws Exception {
    try {
      registry.getAlias(null);
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }
  }

  @Test
  public void testContainsAliasInvalid() throws Exception {
    try {
      registry.containsAlias(null);
      fail();
    }
    catch (AssertionError e) {
      // ignore
    }
  }

  @Test
  public void testRegisterAlias() throws Exception {
    assertFalse(registry.containsAlias("foo"));

    registry.registerAlias("foo", "bar");

    assertTrue(registry.containsAlias("foo"));

    String alias = registry.getAlias("foo");
    assertEquals("bar", alias);
  }

  @Test
  public void testReRegisterAlias() throws Exception {
    testRegisterAlias();

    assertTrue(registry.containsAlias("foo"));

    registry.registerAlias("foo", "baz");

    assertTrue(registry.containsAlias("foo"));

    String alias = registry.getAlias("foo");
    assertEquals("baz", alias);
  }

  @Test
  public void testRemoveAlias() throws Exception {
    assertFalse(registry.containsAlias("foo"));

    try {
      registry.removeAlias("foo");
    }
    catch (NoSuchAliasException e) {
      // expected
    }

    testRegisterAlias();
    registry.removeAlias("foo");
    assertFalse(registry.containsAlias("foo"));
  }

  @Test
  public void testGetAliasNotRegistered() throws Exception {
    assertFalse(registry.containsAlias("foo"));

    try {
      registry.getAlias("foo");
    }
    catch (NoSuchAliasException e) {
      // expected
    }
  }
}