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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.planet57.gshell.repository.internal.TerminalTransferListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

import java.util.List;

/**
 * Resolve dependencies of an artifact.
 *
 * @since 3.0
 */
@Command(name="artifact/resolve-dependencies", description = "Resolve dependencies of an artifact")
public class ResolveDependenciesAction
  extends CommandActionSupport
{
  @Inject
  private RepositoryAccess repositoryAccess;

  @Nullable
  @Option(name="s", longName = "scope", description = "Resolution scope", token = "SCOPE")
  private String scope;

  @Argument(required = true, description = "Artifact coordinates", token = "COORD")
  private String coordinates;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    DefaultRepositorySystemSession session = repositoryAccess.createSession();
    session.setTransferListener(new TerminalTransferListener(context.getIo()));

    Artifact artifact = new DefaultArtifact(coordinates);
    Dependency dependency = new Dependency(artifact, scope);

    CollectRequest collectRequest = new CollectRequest(dependency, repositoryAccess.getRemoteRepositories());
    DependencyRequest request = new DependencyRequest(collectRequest, null);

    if (scope != null) {
      // FIXME: how do we effectivly filter?
      // FIXME: This also seems to do a *lot* more work than what is needed for artifacts that have non-trivial dependencies
      request.setFilter(new DependencyFilter() {
        @Override
        public boolean accept(final DependencyNode node, final List<DependencyNode> parents) {
          Dependency d = node.getDependency();
          if (d != null) {
            log.debug("Accept: {}, optional: {}", d, d.getOptional());
          }
          else {
            log.debug("Accept: {}", node);
          }
          return true;
        }
      });
    }

    log.debug("Resolving dependencies: {}", dependency);
    DependencyResult result = repositoryAccess.getRepositorySystem().resolveDependencies(session, request);

    new DependencyNodePrinter(context.getIo()).print(result.getRoot());

    return null;
  }
}
