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
package com.planet57.gshell.repository.internal;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.repository.RepositoryAccess;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.sonatype.goodies.common.ComponentSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link RepositoryAccess}.
 *
 * @since 3.0
 */
@Named
@Singleton
public class RepositoryAccessImpl
  extends ComponentSupport
  implements RepositoryAccess
{
  private final Branding branding;

  private final RepositorySystem repositorySystem;

  private final LocalRepositoryManagerFactory localRepositoryManagerFactory;

  @Nullable
  private LocalRepository localRepository;

  @Nullable
  private List<RemoteRepository> remoteRepositories;

  @Inject
  public RepositoryAccessImpl(final Branding branding,
                              final RepositorySystem repositorySystem,
                              final LocalRepositoryManagerFactory localRepositoryManagerFactory)
  {
    this.branding = checkNotNull(branding);
    this.repositorySystem = checkNotNull(repositorySystem);
    this.localRepositoryManagerFactory = checkNotNull(localRepositoryManagerFactory);
  }

  @Override
  public RepositorySystem getRepositorySystem() {
    return repositorySystem;
  }

  @Override
  public LocalRepository getLocalRepository() {
    if (localRepository == null) {
      File dir = new File(branding.getUserContextDir(), "repository");
      localRepository = new LocalRepository(dir);
      log.debug("Local-repository: {}", localRepository);
    }
    return localRepository;
  }

  @Override
  public List<RemoteRepository> getRemoteRepositories() {
    if (remoteRepositories == null) {
      List<RemoteRepository> repositories = new ArrayList<>();

      // TODO: make this configurable and persistent

      RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2").build();
      repositories.add(central);
      remoteRepositories = repositories;
      log.debug("Remote-repositories: {}", remoteRepositories);
    }
    return ImmutableList.copyOf(remoteRepositories);
  }

  @Override
  public RepositorySystemSession createSession() {
    DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
    session.setSystemProperties(System.getProperties());

    try {
      session.setLocalRepositoryManager(localRepositoryManagerFactory.newInstance(session, getLocalRepository()));
    }
    catch (NoLocalRepositoryManagerException e) {
      throw new RuntimeException(e);
    }

    // TODO: adjust other session configuration

    return session;
  }
}
