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
package com.planet57.gshell.commands.shell

import com.planet57.gshell.commands.shell.FailAction.FailException
import com.planet57.gshell.commands.shell.FailAction.FailRuntimeException
import com.planet57.gshell.commands.shell.FailAction.FailError
import com.planet57.gshell.commands.shell.FailAction.FailNotification
import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

import static org.junit.Assert.fail

/**
 * Tests for {@link FailAction}.
 */
class FailActionTest
    extends CommandTestSupport
{
  FailActionTest() {
    super(FailAction.class)
  }

  @Test(expected = FailException.class)
  void 'fail with default-message'() {
    executeCommand()
  }

  @Test
  void 'fail default-type with message'() {
    try {
      executeCommand('foo')
      fail()
    }
    catch (FailException e) {
      assert e.message == 'foo'
    }
  }

  @Test
  void 'fail exception with message'() {
    try {
      executeCommand('-t', 'exception', 'foo')
      fail()
    }
    catch (FailException e) {
      assert e.message == 'foo'
    }
  }

  @Test
  void 'fail runtime with message'() {
    try {
      executeCommand('-t', 'runtime', 'foo')
      fail()
    }
    catch (FailRuntimeException e) {
      assert e.message == 'foo'
    }
  }

  @Test
  void 'fail error with message'() {
    try {
      executeCommand('-t', 'error', 'foo')
      fail()
    }
    catch (FailError e) {
      assert e.message == 'foo'
    }
  }

  @Test
  void 'fail notification with message'() {
    try {
      executeCommand('-t', 'notification', 'foo')
      fail()
    }
    catch (FailNotification e) {
      assert e.message == 'foo'
    }
  }
}
