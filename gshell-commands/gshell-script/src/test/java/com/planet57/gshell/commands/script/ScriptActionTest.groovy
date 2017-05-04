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
package com.planet57.gshell.commands.script

import com.planet57.gshell.testharness.CommandTestSupport
import com.planet57.gshell.util.cli2.ProcessingException
import org.junit.Test

/**
 * Tests for {@link ScriptAction}.
 */
class ScriptActionTest
  extends CommandTestSupport
{
  ScriptActionTest() {
    super(ScriptAction.class)
  }

  @Test(expected = ProcessingException.class)
  void 'language required'() {
    executeCommand('-e "57;"')
  }

  @Test(expected = IllegalStateException.class)
  void 'only one source allowed'() {
    executeCommand('-l javascript -e "57;" -u "http://localhost/script.js"')
    executeCommand('-l javascript -e "57;" -f foo.js')
    executeCommand('-l javascript -u "http://localhost/script.js" -f foo.js')
  }

  @Test
  void 'evaluate javascript'() {
    def result = executeCommand('-l javascript -e "57;"')
    assert result == 57
  }

  @Test
  void 'url javascript'() {
    def url = getClass().getResource('test.js')
    assert url != null
    def result = executeCommand("-l javascript -u $url")
    assert result == 57
  }

  @Test
  void 'file javascript'() {
    def url = util.resolveFile("src/test/resources/${getClass().package.name.replace('.', '/')}/test.js")
    assert url != null
    def result = executeCommand("-l javascript -f $url")
    assert result == 57
  }
}
