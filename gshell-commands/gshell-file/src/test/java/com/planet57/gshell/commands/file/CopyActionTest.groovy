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
package com.planet57.gshell.commands.file

import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

/**
 * Tests for {@link CopyAction}.
 */
class CopyActionTest
    extends CommandTestSupport
{
  CopyActionTest() {
    super(CopyAction.class)
  }

  @Test
  void 'copy file'() {
    File dir = util.createTempDir('copy')
    assert dir.exists()

    File source = new File(dir, 'source.txt')
    source.text = System.currentTimeMillis() as String
    assert source.exists()

    File target = new File(dir, 'target.txt')
    assert !target.exists()

    assert executeCommand(source.path, target.path) == null
    assert source.exists()
    assert target.exists()
    assert source.text == target.text
  }

  @Test
  void 'copy file to directory'() {
    File source = util.createTempFile('source.txt')
    source.text = System.currentTimeMillis() as String

    File dir = util.createTempDir('copy')
    assert dir.exists()

    assert executeCommand(source.path, dir.path) == null

    File target = new File(dir, source.name)
    assert target.exists()
    assert source.text == target.text
  }
}
