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
package com.planet57.gshell.repository.internal

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gshell.branding.Branding
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

/**
 * Tests for {@link RepositoryAccessImpl}.
 */
class RepositoryAccessImplTest
  extends TestSupport
{
  RepositoryAccessImpl underTest

  @Mock
  Branding branding

  @Mock
  RepositorySystem repositorySystem

  @Mock
  LocalRepositoryManagerFactory localRepositoryManagerFactory

  @Before
  void setUp() {
    underTest = new RepositoryAccessImpl(branding, repositorySystem, localRepositoryManagerFactory)
  }

  @Test
  void 'default has no remote-repositories'() {
    assert underTest.remoteRepositories != null
    assert underTest.remoteRepositories.empty
  }

  @Test
  void 'remote-repositories list-accessor is immutable'() {
    def repo = new RemoteRepository.Builder('foo', 'default', 'http://example.com').build()
    try {
      underTest.remoteRepositories.add(repo)
      assert false
    }
    catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  void 'duplicate remote-repositories disallowed'() {
    def repo = new RemoteRepository.Builder('foo', 'default', 'http://example.com').build()
    underTest.addRemoteRepository(repo)

    try {
      underTest.addRemoteRepository(repo)
      assert false
    }
    catch (IllegalStateException e) {
      // expected
    }
  }
}
