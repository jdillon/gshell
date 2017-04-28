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
package com.planet57.gshell.commands.standard;

import com.planet57.gshell.testharness.CommandTestSupport;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link SetAction}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SetActionTest
    extends CommandTestSupport
{
  public SetActionTest() {
    super(SetAction.class);
  }

  @Test
  public void testDefineVariable() throws Exception {
    assertFalse(variables.contains("foo"));
    Object result = executeCommand("foo bar");
    assertEqualsSuccess(result);

    assertTrue(variables.contains("foo"));
    Object value = variables.get("foo");
    assertEquals(value, "bar");
  }

  @Test
  public void testRedefineVariable() throws Exception {
    testDefineVariable();
    assertTrue(variables.contains("foo"));

    Object result = executeCommand("foo baz");
    assertEqualsSuccess(result);

    assertTrue(variables.contains("foo"));
    Object value = variables.get("foo");
    assertEquals(value, "baz");
  }

  @Test
  public void testDefineVariableWithExpression() throws Exception {
    assertFalse(variables.contains("foo"));
    Object result = executeCommand("foo ${shell.home}");
    assertEqualsSuccess(result);

    assertTrue(variables.contains("foo"));
    Object value = variables.get("foo");
    Assert.assertEquals(value, variables.get("shell.home", String.class));
  }
}
