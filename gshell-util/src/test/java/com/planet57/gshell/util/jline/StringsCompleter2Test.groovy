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
package com.planet57.gshell.util.jline

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link StringsCompleter2}
 */
class StringsCompleter2Test
  extends TestSupport
{
  StringsCompleter2 underTest

  @Before
  void setUp() {
    underTest = new StringsCompleter2()
  }

  @Test
  void 'initially empty'() {
    assert underTest.strings.empty
    assert underTest.candidates.empty
  }

  @Test
  void 'add strings'() {
    underTest.add('foo')
    assert underTest.strings.size() == 1
    assert underTest.strings.contains('foo')
    assert underTest.candidates.size() == 1

    underTest.add('bar')
    assert underTest.strings.size() == 2
    assert underTest.strings.contains('bar')
    assert underTest.candidates.size() == 2
  }

  @Test
  void 'remove strings'() {
    underTest.add('foo')
    assert underTest.strings.size() == 1
    assert underTest.strings.contains('foo')
    assert underTest.candidates.size() == 1

    underTest.remove('foo')
    assert underTest.strings.empty
    assert underTest.candidates.empty
  }
}
