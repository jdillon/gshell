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
package com.planet57.gshell.commands.artifact;

import com.google.common.collect.ImmutableList;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.IO;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;

/**
 * ???
 *
 * @since 3.0
 */
@Command(name="artifact/resolve", description = "Resolve an artifact")
public class ResolveAction
  extends CommandActionSupport
{
  @Inject
  RepositorySystem repositorySystem;

  @Inject
  LocalRepositoryManagerFactory localRepositoryManagerFactory;

  @Argument(required = true)
  private String coordinates;

  @Override
  public Object execute(final @Nonnull CommandContext context) throws Exception {
    IO io = context.getIo();
    Branding branding = context.getShell().getBranding();

    File repositoryDir = new File(branding.getUserContextDir(), "repository");
    LocalRepository localRepository = new LocalRepository(repositoryDir);
    log.debug("Local-repository: {}", localRepository);

    DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
    session.setLocalRepositoryManager(localRepositoryManagerFactory.newInstance(session, localRepository));

    RemoteRepository remoteRepository = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2").build();
    log.debug("Remote-repository: {}", remoteRepository);

    Artifact artifact = new DefaultArtifact(coordinates);
    log.debug("Resolving: {}", artifact);

    ArtifactRequest request = new ArtifactRequest(artifact, ImmutableList.of(remoteRepository), null);
    ArtifactResult result = repositorySystem.resolveArtifact(session, request);
    log.debug("Result: {}", result);

    io.println(result.getArtifact().getFile());

    return null;
  }
}
