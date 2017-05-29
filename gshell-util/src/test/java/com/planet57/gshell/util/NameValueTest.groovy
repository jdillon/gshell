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
package com.planet57.gshell.util

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

/**
 * Tests for {@link NameValue}.
 */
class NameValueTest
  extends TestSupport
{
  @Test
  void 'name=value'() {
    def nv = NameValue.parse('foo=bar')
    assert nv.name == 'foo'
    assert nv.value == 'bar'
  }

  @Test
  void 'name'() {
    def nv = NameValue.parse('foo')
    assert nv.name == 'foo'
    assert nv.value == Boolean.TRUE.toString()
  }

  @Test
  void 'name=value toString'() {
    def nv = NameValue.parse('foo=bar')
    assert nv.toString() == "foo='bar'"
  }
}
