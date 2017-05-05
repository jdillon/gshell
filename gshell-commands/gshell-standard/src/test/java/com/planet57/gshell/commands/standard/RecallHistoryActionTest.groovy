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
import com.planet57.gshell.util.converter.ConversionException
import com.planet57.gshell.variables.Variables
import org.jline.reader.History
import org.junit.Test

/**
 * Tests for {@link RecallHistoryAction}.
 */
class RecallHistoryActionTest
    extends CommandTestSupport
{
  RecallHistoryActionTest() {
    super(RecallHistoryAction.class)
  }

  @Override
  void setUp() {
    requiredCommands.put('set', SetAction.class)
    super.setUp()
  }

  @Test(expected = ProcessingException.class)
  void 'too many arguments'() {
    executeCommand('1 2')
  }

  @Test(expected = IllegalArgumentException.class)
  void 'index out of range'() {
    executeCommand(Integer.MAX_VALUE as String)
  }

  @Test(expected = ConversionException.class)
  void 'invalid index'() {
    executeCommand('foo')
  }

  @Test
  void testRecallElement() {
    History history = shell.history
    Variables variables = shell.variables

    // Clear history and make sure there is no foo variable
    history.purge()
    assert !variables.contains('foo')

    // Then add 2 items, both setting foo
    history.add('set foo bar')
    history.add('set foo baz')
    assert history.size() == 2

    // Recall the first, which sets foo to bar
    Object result = executeCommand('1')
    assert result == null

    // Make sure it executed
    assert variables.get('foo') == 'bar'
  }
}
