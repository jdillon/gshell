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

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.IO;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Resolve an artifact.
 *
 * @since 3.0
 */
@Command(name="artifact/resolve", description = "Resolve an artifact")
public class ResolveAction
  extends CommandActionSupport
{
  @Inject
  private RepositoryAccess repositoryAccess;

  @Argument(required = true, description = "Artifact coordinates", token = "COORD")
  private String coordinates;

  @Override
  public Object execute(final @Nonnull CommandContext context) throws Exception {
    RepositorySystemSession session = repositoryAccess.createSession();

    Artifact artifact = new DefaultArtifact(coordinates);
    log.debug("Resolving: {}", artifact);

    ArtifactRequest request = new ArtifactRequest(artifact, repositoryAccess.getRemoteRepositories(), null);
    ArtifactResult result = repositoryAccess.getRepositorySystem().resolveArtifact(session, request);
    log.debug("Result: {}", result);

    IO io = context.getIo();
    io.println(result.getArtifact().getFile());

    return null;
  }
}
