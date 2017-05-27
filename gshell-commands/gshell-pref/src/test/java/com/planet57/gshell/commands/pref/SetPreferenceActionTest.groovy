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
package com.planet57.gshell.commands.pref

import org.junit.Test

/**
 * Tests for {@link SetPreferenceAction}.
 */
class SetPreferenceActionTest
  extends PreferenceActionTestSupport
{
  SetPreferenceActionTest() {
    super(SetPreferenceAction.class)
  }

  @Test
  void 'set user preference'() {
    assert executeCommand(PATH, 'foo', 'bar') == null

    def root = userRoot()
    def node = root.node(PATH)
    assert node.get('foo', null) == 'bar'
  }

  @Test
  void 'set system preference'() {
    assert executeCommand('-s', PATH, 'foo', 'bar') == null

    def root = systemRoot()
    def node = root.node(PATH)
    assert node.get('foo', null) == 'bar'
  }
}
