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
import com.planet57.gshell.util.cli2.ProcessingException
import org.junit.Test

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

  @Test(expected = ProcessingException.class)
  void 'too many arguments'() {
    executeCommand('1 2')
  }

  @Test
  void purge() {
    // Make sure there is going to be more than one item in history
    executeLine('echo 1')
    executeLine('echo 2')

    // Then purge and expect history to be empty
    assert executeCommand('-p') == null
    assert shell.history.empty
  }

  @Test
  void 'list subset'() {
    // first purge
    purge()

    // Then seed 10 items
    10.times {
      executeLine("echo $it")
    }

    // And then ask for the last 5
    assert executeCommand('5') == null
  }

  @Test
  void 'list overset'() {
    // first purge
    purge()

    // Then seed 10 items
    10.times {
      executeLine("echo $it")
    }

    // And then ask for the last 15
    assert executeCommand('15') == null
  }
}
