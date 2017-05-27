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

/**
 * Tests for {@link SetAction}.
 */
class SetActionTest
    extends CommandTestSupport
{
  SetActionTest() {
    super(SetAction.class)
  }

  Variables variables

  @Override
  void setUp() {
    super.setUp()
    variables = shell.variables
  }

  @Test
  void 'define variable'() {
    assert !variables.contains('foo')
    assert executeCommand('foo bar') == null

    assert variables.contains('foo')
    assert variables.get('foo') == 'bar'
  }

  @Test
  void 'redefine variable'() {
    assert !variables.contains('foo')
    assert executeCommand('foo bar') == null

    assert variables.contains('foo')
    assert variables.get('foo') == 'bar'
    assert variables.contains('foo')

    assert executeCommand('foo baz') == null

    assert variables.contains('foo')
    assert variables.get('foo') == 'baz'
  }

  @Test
  void 'define variable with expression'() {
    assert !variables.contains('foo')
    assert executeCommand('foo ${shell.home}') == null

    assert variables.contains('foo')
    def value = variables.get('foo')
    assert variables.get('shell.home', String.class) == value
  }
}
