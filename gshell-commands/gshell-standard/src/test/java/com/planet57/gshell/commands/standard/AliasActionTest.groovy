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
import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

/**
 * Tests for {@link AliasAction}.
 */
class AliasActionTest
    extends CommandTestSupport
{
  @Inject
  AliasRegistry aliasRegistry

  AliasActionTest() {
    super(AliasAction.class)
  }

  @Test
  void 'define alias'() {
    assert !aliasRegistry.containsAlias('foo')

    assert executeCommand('foo bar') == null

    assert aliasRegistry.containsAlias('foo')
    assert aliasRegistry.getAlias('foo') == 'bar'
  }

  @Test
  void 'redefine alias'() {
    assert !aliasRegistry.containsAlias('foo')

    assert executeCommand('foo bar') == null

    assert aliasRegistry.containsAlias('foo')
    assert aliasRegistry.getAlias('foo') == 'bar'
    assert aliasRegistry.containsAlias('foo')

    assert executeCommand('foo baz') == null

    assert aliasRegistry.containsAlias('foo')
    assert aliasRegistry.getAlias('foo') == 'baz'
  }

  @Test
  void 'execute alias'() {
    assert !aliasRegistry.containsAlias('make-alias')

    assert executeCommand('make-alias alias') == null

    assert aliasRegistry.containsAlias('make-alias')
    assert aliasRegistry.getAlias('make-alias') == 'alias'

    assert executeLine('make-alias foo bar') == null

    assert aliasRegistry.containsAlias('foo')
    assert aliasRegistry.getAlias('foo') == 'bar'
  }
}
