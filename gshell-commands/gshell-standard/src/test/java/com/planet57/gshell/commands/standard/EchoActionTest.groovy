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
 * Tests for {@link EchoAction}.
 */
class EchoActionTest
    extends CommandTestSupport
{
  EchoActionTest() {
    super(EchoAction.class)
  }

  private static final String NEWLINE = System.getProperty('line.separator')

  @Test
  void 'echo a b c'() {
    assert executeCommand('a b c') == null
    assert io.outputString == 'a b c' + NEWLINE
  }

  @Test
  void 'echo variable'() {
    assert executeCommand('${shell.home}') == null
    assert io.outputString == shell.variables.get('shell.home', String.class) + NEWLINE
  }

  @Test
  void 'echo with stop'() {
    assert executeCommand('-- -D') == null
    assert io.outputString == '-D' + NEWLINE
  }

  @Test
  void 'echo with padding'() {
    assert executeCommand("' foo '") == null
    assert io.outputString == ' foo ' + NEWLINE
  }

  @Test
  void 'echo omit newline'() {
    assert executeCommand('-n foo') == null
    assert io.outputString == 'foo'
  }
}
