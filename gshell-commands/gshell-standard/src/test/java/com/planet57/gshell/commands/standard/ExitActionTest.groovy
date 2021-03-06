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

import com.planet57.gshell.command.CommandAction.ExitNotification
import com.planet57.gshell.testharness.CommandTestSupport
import com.planet57.gshell.util.cli2.ProcessingException
import org.junit.Test

import static org.junit.Assert.fail

/**
 * Tests for {@link ExitAction}.
 */
class ExitActionTest
    extends CommandTestSupport
{
  ExitActionTest() {
    super(ExitAction.class)
  }

  @Test
  void 'exit with code'() {
    try {
      executeCommand('57')
      fail()
    }
    catch (ExitNotification notification) {
      assert notification.code == 57
    }
  }

  @Test(expected = ProcessingException.class)
  void 'too many arguments'() {
    executeCommand('1 2')
  }

  @Test(expected = Exception.class)
  void 'unparseable return code'() {
    executeCommand('foo')
  }
}
