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
import com.planet57.gshell.util.converter.ConversionException;
import org.jline.reader.History;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link RecallHistoryAction}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RecallHistoryActionTest
    extends CommandTestSupport
{
  public RecallHistoryActionTest() {
    super(RecallHistoryAction.class);
  }

  @Override
  public void setUp() throws Exception {
    requiredCommands.put("set", SetAction.class);
    super.setUp();
  }

  @Test
  public void testTooManyArguments() throws Exception {
    try {
      executeCommand("1 2");
      fail();
    }
    catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testIndexOutOfRange() throws Exception {
    Object result = executeCommand(String.valueOf(Integer.MAX_VALUE));
    assertEqualsFailure(result);
  }

  @Test
  public void testInvalidIndex() throws Exception {
    try {
      executeCommand("foo");
      fail();
    }
    catch (ConversionException e) {
      // expected
    }
  }

  @Test
  public void testRecallElement() throws Exception {
    History history = getShell().getHistory();

    // Clear history and make sure there is no foo variable
    history.purge();
    assertFalse(variables.contains("foo"));

    // Then add 2 items, both setting foo
    history.add("set foo bar");
    history.add("set foo baz");
    assertEquals(2, getShell().getHistory().size());

    // Recall the first, which sets foo to bar
    Object result = executeCommand("1");
    assertEqualsSuccess(result);

    // Make sure it executed
    assertEquals("bar", variables.get("foo"));
  }
}
