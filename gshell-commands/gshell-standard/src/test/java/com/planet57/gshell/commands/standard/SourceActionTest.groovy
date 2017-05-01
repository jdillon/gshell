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
import com.planet57.gshell.variables.Variables
import org.junit.Test

import static org.junit.Assert.fail

/**
 * Tests for {@link SourceAction}.
 */
class SourceActionTest
    extends CommandTestSupport
{
  SourceActionTest() {
    super(SourceAction.class)
  }

  @Override
  void setUp() {
    requiredCommands.put('set', SetAction.class)
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
  void testNoSuchFile() {
    try {
      executeCommand('no-such-file')
      fail()
    }
    catch (FileNotFoundException e) {
      // expected
    }
  }

  @Test
  void test1() {
    URL script = getClass().getResource('test1.tsh')
    assert script != null
    Object result = executeCommand(script.toExternalForm())
    assert result == null
  }

  @Test
  void test2() {
    Variables variables = shell.variables

    assert !variables.contains('foo')

    URL script = getClass().getResource('test2.tsh')
    assert script != null
    Object result = executeCommand(script.toExternalForm())
    assert result == null

    assert variables.contains('foo')
    Object value = variables.get('foo')
    assert value == 'bar'
  }
}
