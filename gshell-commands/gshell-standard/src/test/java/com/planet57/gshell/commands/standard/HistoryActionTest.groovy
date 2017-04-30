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
package com.planet57.gshell.commands.standard

import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

import static org.junit.Assert.fail

/**
 * Tests for {@link HistoryAction}.
 */
class HistoryActionTest
    extends CommandTestSupport
{
  HistoryActionTest() {
    super(HistoryAction.class)
  }

  @Override
  void setUp() {
    requiredCommands.put('echo', EchoAction.class)
    super.setUp()
  }

  @Test
  void testTooManyArguments() {
    try {
      executeCommand('1 2')
      fail()
    }
    catch (Exception e) {
      // expected
    }
  }

  @Test
  void testPurge() {
    // Make sure there is going to be more than one item in history
    executeLine('echo 1')
    executeLine('echo 2')

    // Then purge and expect history to be empty
    Object result = executeCommand('-p')
    assertEqualsSuccess(result)

    assert shell.history.empty
  }

  @Test
  void testListSubset() {
    // first purge
    testPurge()

    // Then seed 10 items
    10.times {
      executeLine("echo $it")
    }

    // And then ask for the last 5
    Object result = executeCommand('5')
    assertEqualsSuccess(result)

    // TODO: Verify output
  }

  @Test
  void testListOverset() {
    // first purge
    testPurge()

    // Then seed 10 items
    10.times {
      executeLine("echo $it")
    }

    // And then ask for the last 15
    Object result = executeCommand('15')
    assertEqualsSuccess(result)

    // TODO: Verify output
  }
}
