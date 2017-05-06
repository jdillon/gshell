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
 * Tests for {@link ExportPreferencesAction}.
 */
class ExportPreferencesActionTest
    extends PreferenceActionTestSupport
{
  ExportPreferencesActionTest() {
    super(ExportPreferencesAction.class)
  }

  @Test
  void 'export user preference'() {
    def root = userRoot()
    def node = root.node(PATH)
    node.put('foo', 'bar')
    node.sync()

    assert executeCommand(PATH) == null

    assert io.outputString == """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE preferences SYSTEM "http://java.sun.com/dtd/preferences.dtd">
<preferences EXTERNAL_XML_VERSION="1.0">
  <root type="user">
    <map/>
    <node name="test">
      <map/>
      <node name="${ID}">
        <map/>
        <node name="com">
          <map/>
          <node name="planet57">
            <map/>
            <node name="gshell">
              <map/>
              <node name="commands">
                <map/>
                <node name="pref">
                  <map>
                    <entry key="foo" value="bar"/>
                  </map>
                </node>
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
  </root>
</preferences>
"""
  }
}
