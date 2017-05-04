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
package com.planet57.gshell.variables;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link VariablesSupport}.
 */
public class VariablesSupportTest
  extends TestSupport
{
  private VariablesSupport underTest;

  @Before
  public void setUp() {
    underTest = new VariablesSupport();
  }

  @Test
  public void testSet() throws Exception {
    String name = "a";
    Object value = new Object();

    assertFalse(underTest.contains(name));
    underTest.set(name, value);
    assertTrue(underTest.contains(name));

    Object obj = underTest.get(name);
    assertEquals(value, obj);

    String str = underTest.names().iterator().next();
    assertEquals(name, str);
  }

  @Test
  public void testGet() throws Exception {
    String name = "a";
    Object value = new Object();

    Object obj1 = underTest.get(name);
    assertNull(obj1);

    underTest.set(name, value);
    Object obj2 = underTest.get(name);
    assertSame(value, obj2);
  }

  @Test
  public void testGetUsingDefault() throws Exception {
    String name = "a";
    Object value = new Object();

    Object obj1 = underTest.get(name);
    assertNull(obj1);

    Object obj2 = underTest.get(name, value);
    assertSame(value, obj2);
  }

  @Test
  public void testNames() throws Exception {
    Iterator<String> iter = underTest.names().iterator();
    assertNotNull(iter);
    assertFalse(iter.hasNext());
  }

  @Test
  public void testNamesImmutable() throws Exception {
    underTest.set("a", "b");

    Iterator<String> iter = underTest.names().iterator();
    iter.next();

    try {
      iter.remove();
    }
    catch (UnsupportedOperationException expected) {
      // ignore
    }
  }
}
