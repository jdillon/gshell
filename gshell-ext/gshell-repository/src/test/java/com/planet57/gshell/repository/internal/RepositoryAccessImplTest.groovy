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
