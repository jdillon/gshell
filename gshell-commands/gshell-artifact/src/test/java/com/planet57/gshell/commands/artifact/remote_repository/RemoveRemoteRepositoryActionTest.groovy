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
import org.eclipse.aether.repository.RemoteRepository
import org.junit.Test

/**
 * Tests for {@link RemoveRemoteRepositoryAction}.
 */
class RemoveRemoteRepositoryActionTest
  extends CommandTestSupport
{
  @Inject
  RepositoryAccess repositoryAccess

  RemoveRemoteRepositoryActionTest() {
    super(RemoveRemoteRepositoryAction.class)
  }

  @Test
  void 'remove repository'() {
    assert repositoryAccess.remoteRepositories.empty

    def repo = new RemoteRepository.Builder('example', 'default', 'http://example.com/').build()
    repositoryAccess.addRemoteRepository(repo)
    assert repositoryAccess.remoteRepositories.size() == 1

    assert executeCommand('example') == null

    assert repositoryAccess.remoteRepositories.empty
  }

  @Test
  void 'remove repository unknown-id fails'() {
    assert repositoryAccess.remoteRepositories.empty

    try {
      executeCommand('example')
      assert false
    }
    catch (IllegalStateException e) {
      // expected
    }
  }
}
