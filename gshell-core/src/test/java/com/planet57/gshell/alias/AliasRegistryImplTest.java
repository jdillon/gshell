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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.internal.EventManagerImpl;
import com.planet57.gshell.internal.AliasRegistryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

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
  extends TestSupport
{
  private AliasRegistryImpl underTest;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, (Module) binder -> {
      binder.bind(EventManager.class).to(EventManagerImpl.class);
      binder.bind(AliasRegistry.class).to(AliasRegistryImpl.class);
    });
    underTest = injector.getInstance(AliasRegistryImpl.class);
  }

  @After
  public void tearDown() {
    underTest = null;
  }

  @Test
  public void testRegisterAliasInvalid() throws Exception {
    try {
      underTest.registerAlias(null, null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      underTest.registerAlias("foo", null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      underTest.registerAlias(null, "foo");
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }
  }

  @Test
  public void testRemoveAliasInvalid() throws Exception {
    try {
      underTest.removeAlias(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }
  }

  @Test
  public void testGetAliasInvalid() throws Exception {
    try {
      underTest.getAlias(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }
  }

  @Test
  public void testContainsAliasInvalid() throws Exception {
    try {
      underTest.containsAlias(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }
  }

  @Test
  public void testRegisterAlias() throws Exception {
    assertFalse(underTest.containsAlias("foo"));

    underTest.registerAlias("foo", "bar");

    assertTrue(underTest.containsAlias("foo"));

    String alias = underTest.getAlias("foo");
    assertEquals("bar", alias);
  }

  @Test
  public void testReRegisterAlias() throws Exception {
    testRegisterAlias();

    assertTrue(underTest.containsAlias("foo"));

    underTest.registerAlias("foo", "baz");

    assertTrue(underTest.containsAlias("foo"));

    String alias = underTest.getAlias("foo");
    assertEquals("baz", alias);
  }

  @Test
  public void testRemoveAlias() throws Exception {
    assertFalse(underTest.containsAlias("foo"));

    try {
      underTest.removeAlias("foo");
    }
    catch (AliasRegistry.NoSuchAliasException e) {
      // expected
    }

    testRegisterAlias();
    underTest.removeAlias("foo");
    assertFalse(underTest.containsAlias("foo"));
  }

  @Test
  public void testGetAliasNotRegistered() throws Exception {
    assertFalse(underTest.containsAlias("foo"));

    try {
      underTest.getAlias("foo");
    }
    catch (AliasRegistry.NoSuchAliasException e) {
      // expected
    }
  }
}
