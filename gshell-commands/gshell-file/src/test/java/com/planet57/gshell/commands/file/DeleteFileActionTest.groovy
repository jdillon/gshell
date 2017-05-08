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
import com.planet57.gshell.util.io.FileAssert
import org.junit.Test

/**
 * Tests for {@link DeleteFileAction}.
 */
class DeleteFileActionTest
    extends CommandTestSupport
{
  DeleteFileActionTest() {
    super(DeleteFileAction.class)
  }

  @Test
  void 'delete file'() {
    File file = util.createTempFile('delete-file')
    assert file.exists()
    assert executeCommand(file.path) == null
    assert !file.exists()
  }

  @Test
  void 'delete directory recursive'() {
    File dir = util.createTempDir('delete-file')
    10.times {
      new File(dir, "child${it}.txt").text = System.currentTimeMillis() as String
    }
    assert dir.exists()
    assert executeCommand('-r', dir.path) == null
    assert !dir.exists()
  }

  @Test(expected = FileAssert.AssertionException.class)
  void 'delete directory non-recursive fails'() {
    File dir = util.createTempDir('delete-file')
    assert dir.exists()
    executeCommand(dir.path)
  }
}
