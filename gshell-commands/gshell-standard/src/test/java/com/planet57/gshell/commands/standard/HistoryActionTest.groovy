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
    requiredCommands.put('set', SetAction.class)
    super.setUp()
  }

  @Test
  void purge() {
    // Make sure there is going to be more than one item in history
    executeLine('set foo 1')
    executeLine('set foo 2')
    assert shell.variables.get('foo', Integer.class) == 2

    // FIXME: this is not really ideal since history is only appended for interactive-shells

    // Then purge and expect history to be empty
    assert executeCommand('-p') == null
    assert shell.history.empty
  }
}
