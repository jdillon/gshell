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
package com.planet57.gshell.commands.artifact.remote_repository

import javax.inject.Inject

import com.planet57.gshell.repository.RepositoryAccess
import com.planet57.gshell.testharness.CommandTestSupport
import org.junit.Test

/**
 * Tests for {@link AddRemoteRepositoryAction}.
 */
class AddRemoteRepositoryActionTest
  extends CommandTestSupport
{
  @Inject
  RepositoryAccess repositoryAccess

  AddRemoteRepositoryActionTest() {
    super(AddRemoteRepositoryAction.class)
  }

  @Test
  void 'add repository'() {
    assert repositoryAccess.remoteRepositories.empty

    assert executeCommand('example', 'http://example.com/') == null

    def repos = repositoryAccess.remoteRepositories
    assert repos.size() == 1
    def repo = repos[0]
    assert repo.id == 'example'
    assert repo.contentType =='default'
    assert repo.url == 'http://example.com/'
  }

  @Test
  void 'add repository with duplicate-id fails'() {
    assert repositoryAccess.remoteRepositories.empty

    assert executeCommand('example', 'http://example.com/') == null

    try {
      executeCommand('example', 'http://example.com/')
      assert false
    }
    catch (IllegalStateException e) {
      // expected
    }
  }
}
