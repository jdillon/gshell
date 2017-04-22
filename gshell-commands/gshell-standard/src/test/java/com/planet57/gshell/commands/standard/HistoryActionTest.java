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

import static org.junit.Assert.fail;

/**
 * Tests for the {@link HistoryAction}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class HistoryActionTest
    extends CommandTestSupport
{
  public HistoryActionTest() {
    super(HistoryAction.class);
  }

  @Override
  public void setUp() throws Exception {
    requiredCommands.put("echo", EchoAction.class);
    super.setUp();
  }

  @Test
  public void testTooManyArguments() throws Exception {
    try {
      executeWithArgs("1 2");
      fail();
    }
    catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testPurge() throws Exception {
    // Make sure there is going to be more than one item in history
    execute("echo 1");
    execute("echo 2");

    // Then purge and expect history to be empty
    Object result = executeWithArgs("-p");
    assertEqualsSuccess(result);

    Assert.assertEquals(0, getShell().getHistory().size());
  }

  @Test
  public void testListSubset() throws Exception {
    // first purge
    testPurge();

    // Then seed 10 items
    for (int i = 0; i < 10; i++) {
      execute("echo " + i);
    }

    // And then ask for the last 5
    Object result = executeWithArgs("5");
    assertEqualsSuccess(result);

    // TODO: Verify output
  }

  @Test
  public void testListOverset() throws Exception {
    // first purge
    testPurge();

    // Then seed 10 items
    for (int i = 0; i < 10; i++) {
      execute("echo " + i);
    }

    // And then ask for the last 15
    Object result = executeWithArgs("15");
    assertEqualsSuccess(result);

    // TODO: Verify output
  }
}
