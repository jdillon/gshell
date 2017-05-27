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
import com.planet57.gshell.repository.RepositoryAccess;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.sonatype.goodies.common.ComponentSupport;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
  private final RepositorySystem repositorySystem;

  private final LocalRepositoryManagerFactory localRepositoryManagerFactory;

  @Nullable
  private LocalRepository localRepository;

  private final Map<String, RemoteRepository> remoteRepositories = new ConcurrentHashMap<>();

  @Inject
  public RepositoryAccessImpl(final RepositorySystem repositorySystem,
                              final LocalRepositoryManagerFactory localRepositoryManagerFactory)
  {
    this.repositorySystem = checkNotNull(repositorySystem);
    this.localRepositoryManagerFactory = checkNotNull(localRepositoryManagerFactory);
  }

  @Override
  public RepositorySystem getRepositorySystem() {
    return repositorySystem;
  }

  @Override
  public LocalRepository getLocalRepository() {
    checkState(localRepository != null, "Local-repository not configured");
    return localRepository;
  }

  @Override
  public void setLocalRepository(final LocalRepository repository) {
    checkNotNull(repository);
    log.debug("Local-repository: {}", repository);
    this.localRepository = repository;
  }

  @Override
  public List<RemoteRepository> getRemoteRepositories() {
    return ImmutableList.copyOf(remoteRepositories.values());
  }

  @Override
  public void addRemoteRepository(final RemoteRepository repository) {
    checkNotNull(repository);
    String id = repository.getId();
    synchronized (remoteRepositories) {
      checkState(!remoteRepositories.containsKey(id), "Duplicate repository ID: %s", id);
      remoteRepositories.put(id, repository);
      log.debug("Added remote-repository: {}", repository);
    }
  }

  @Override
  public void removeRemoteRepository(final String id) {
    checkNotNull(id);
    checkState(remoteRepositories.remove(id) != null, "Unknown repository ID: %s", id);
    log.debug("Removed remote-repository: {}", id);
  }

  @Override
  public DefaultRepositorySystemSession createSession() {
    // create new maven-like session; pending if we want to control this impl more or not
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    session.setRepositoryListener(new LoggingRepositoryListener());
    session.setCache(new DefaultRepositoryCache());

    // TODO: adjust other session configuration, expose for configuration
    session.setOffline(false);
    session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_IGNORE);
    session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_ALWAYS);

    // TODO: install mirror-selector, expose for configuration & likely adjust for concurrent access?  Could not find where this is used in maven3 however.

    try {
      // according to the javadocs, this should be done as one of the last steps to setup a new session
      session.setLocalRepositoryManager(localRepositoryManagerFactory.newInstance(session, getLocalRepository()));
    }
    catch (NoLocalRepositoryManagerException e) {
      throw new RuntimeException(e);
    }

    return session;
  }
}
