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
  void testEcho_a_b_c() {
    Object result = executeCommand('a b c')
    assertEqualsSuccess(result)
    assertOutputEquals('a b c' + NEWLINE)
  }

  @Test
  void testEcho_$shell_home() {
    Object result = executeCommand('${shell.home}')
    assertEqualsSuccess(result)
    assertOutputEquals(shell.variables.get('shell.home', String.class) + NEWLINE)
  }

  @Test
  void testEchoWithStop() {
    Object result = executeCommand('-- -D')
    assertEqualsSuccess(result)
    assertOutputEquals('-D' + NEWLINE)
  }

  @Test
  void testEchoWithSpacePadding() {
    Object result = executeCommand("' foo '")
    assertEqualsSuccess(result)
    assertOutputEquals(' foo ' + NEWLINE)
  }

  @Test
  void testEchoNoNewline() {
    Object result = executeCommand('-n foo')
    assertEqualsSuccess(result)
    assertOutputEquals('foo')
  }
}
