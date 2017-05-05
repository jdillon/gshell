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

import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

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

  private void assertOutputEquals(final String expected) {
    assertThat(io.outputString, is(expected));
  }

  @Test
  void 'echo a b c'() {
    Object result = executeCommand('a b c')
    assert result == null
    assertOutputEquals('a b c' + NEWLINE)
  }

  @Test
  void 'echo variable'() {
    Object result = executeCommand('${shell.home}')
    assert result == null
    assertOutputEquals(shell.variables.get('shell.home', String.class) + NEWLINE)
  }

  @Test
  void 'echo with stop'() {
    Object result = executeCommand('-- -D')
    assert result == null
    assertOutputEquals('-D' + NEWLINE)
  }

  @Test
  void 'echo with padding'() {
    Object result = executeCommand("' foo '")
    assert result == null
    assertOutputEquals(' foo ' + NEWLINE)
  }

  @Test
  void 'echo omit newline'() {
    Object result = executeCommand('-n foo')
    assert result == null
    assertOutputEquals('foo')
  }
}
