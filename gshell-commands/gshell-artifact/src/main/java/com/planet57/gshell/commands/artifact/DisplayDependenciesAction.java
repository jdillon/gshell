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

import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.repository.internal.IOTransferListener;
import com.planet57.gshell.util.cli2.Option;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.IO;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Display dependencies of an artifact.
 *
 * @since 3.0
 */
@Command(name="artifact/display-dependencies", description = "Display dependencies of an artifact")
public class DisplayDependenciesAction
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
    session.setTransferListener(new IOTransferListener(context.getIo()));

    Artifact artifact = new DefaultArtifact(coordinates);
    Dependency dependency = new Dependency(artifact, scope);
    CollectRequest request = new CollectRequest(dependency, repositoryAccess.getRemoteRepositories());

    log.debug("Collecting dependencies: {}", dependency);
    CollectResult result = repositoryAccess.getRepositorySystem().collectDependencies(session, request);

    IO io = context.getIo();
    print(io, result.getRoot(), "");

    return null;
  }

  private static void print(final IO io, final DependencyNode node, final String indent) {
    AttributedStringBuilder buff = new AttributedStringBuilder();
    buff.style(AttributedStyle.DEFAULT);

    Dependency dependency = node.getDependency();
    Artifact artifact = node.getArtifact();

    buff.append(artifact.getGroupId());
    faint(buff, ":");
    buff.append(artifact.getArtifactId());
    faint(buff, ":");
    buff.append(artifact.getExtension());
    faint(buff, ":");

    String classifier = artifact.getClassifier();
    if (classifier.length() > 0) {
      buff.append(classifier);
      faint(buff, ":");
    }

    // FIXME: for some reason this is not BOLD as it should be
    buff.style(AttributedStyle.DEFAULT.bold());
    buff.append(artifact.getVersion());
    buff.style(AttributedStyle.DEFAULT.boldOff());

    if (dependency != null) {
      faint(buff, " (" + dependency.getScope() + ")");
    }

    io.format("%s%s%n", indent, buff.toAnsi(io.terminal));
    node.getChildren().forEach(child -> print(io, child, indent + "  "));
  }

  private static void faint(final AttributedStringBuilder buff, final String text) {
    buff.style(AttributedStyle.DEFAULT.faint());
    buff.append(text);
    buff.style(AttributedStyle.DEFAULT.faintOff());
  }
}
