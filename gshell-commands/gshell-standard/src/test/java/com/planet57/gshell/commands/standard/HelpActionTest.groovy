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

import javax.inject.Inject

import com.planet57.gshell.alias.AliasRegistry
import com.planet57.gshell.command.registry.CommandRegistry
import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

/**
 * Tests for {@link HelpAction}.
 */
class HelpActionTest
    extends CommandTestSupport
{
  @Inject
  private AliasRegistry aliasRegistry

  @Inject
  private CommandRegistry commandRegistry

  HelpActionTest() {
    super(HelpAction.class)
  }

  @Test
  void testHelpHelp() {
    assert commandRegistry.containsCommand('help')
    assert !aliasRegistry.containsAlias('foo')
    Object result = executeCommand('help')
    assertEqualsSuccess(result)
  }

  @Test
  void testHelpFoo() {
    assert !commandRegistry.containsCommand('foo')
    assert !aliasRegistry.containsAlias('foo')
    Object result = executeCommand('foo')
    assertEqualsFailure(result)
  }
}
