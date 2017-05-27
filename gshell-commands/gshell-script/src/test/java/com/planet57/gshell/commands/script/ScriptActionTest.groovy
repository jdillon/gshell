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

  private File resolveFile(final String name) {
    def file = util.resolveFile("src/test/resources/${getClass().package.name.replace('.', '/')}/$name")
    assert file != null
    return file
  }

  private URL resolveUrl(final String name) {
    def url = getClass().getResource(name)
    assert url != null
    return url
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

  //
  // javascript; standard on jvm
  //

  @Test
  void 'evaluate javascript'() {
    assert executeCommand('-l javascript -e "57;"') == 57
  }

  @Test
  void 'url javascript'() {
    def url = resolveUrl('test.js')
    assert executeCommand("-l javascript -u $url") == 57
  }

  @Test
  void 'file javascript'() {
    def file = resolveFile('test.js')
    assert executeCommand("-l javascript -f $file") == 57
  }

  //
  // groovy via gshell-groovy
  //

  @Test
  void 'evaluate groovy'() {
    assert executeCommand('-l groovy -e "return 57"') == 57
  }

  @Test
  void 'url groovy'() {
    def url = resolveUrl('test.groovy')
    assert executeCommand("-l groovy -u $url") == 57
  }

  @Test
  void 'file groovy'() {
    def file = resolveFile('test.groovy')
    assert executeCommand("-l groovy -f $file") == 57
  }
}
