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
package com.planet57.gshell.commands.plugin;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.planet57.gshell.internal.BeanContainer;
import com.planet57.gshell.shell.Shell;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.repository.RepositoryAccess;
import com.planet57.gshell.repository.internal.TerminalTransferListener;
import com.planet57.gshell.util.cli2.Argument;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Load a plugin.
 *
 * @since 3.0
 */
@Command(name="plugin/load", description = "Load a plugin")
public class LoadPluginAction
  extends CommandActionSupport
{
  @Inject
  private BeanContainer container;

  @Inject
  private RepositoryAccess repositoryAccess;

  @Argument(required = true, description = "Artifact coordinates", token = "COORD")
  private String coordinates;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    DefaultRepositorySystemSession session = repositoryAccess.createSession();
    session.setTransferListener(new TerminalTransferListener(context.getIo()));

    Artifact artifact = new DefaultArtifact(coordinates);
    Dependency dependency = new Dependency(artifact, null);

    // TODO: need to figure out how to effectively exclude gshell-core and related bits provided by the runtime which would be invalid to duplicate

    CollectRequest collectRequest = new CollectRequest(dependency, repositoryAccess.getRemoteRepositories());
    DependencyRequest request = new DependencyRequest(collectRequest, null);

    log.debug("Resolving dependencies: {}", dependency);
    DependencyResult result = repositoryAccess.getRepositorySystem().resolveDependencies(session, request);

    URL[] classPath = result.getArtifactResults().stream()
      .map(artifactResult -> url(artifactResult.getArtifact().getFile()))
      .toArray(URL[]::new);

    log.debug("Classpath:");
    for (URL url : classPath) {
      log.debug("  {}", url);
    }

    ClassLoader cl = new URLClassLoader(classPath, Shell.class.getClassLoader());
    log.debug("Class-loader: {}", cl);

    URLClassSpace classSpace = new URLClassSpace(cl, classPath);

    List<Module> modules = new ArrayList<>();
    modules.add(new SpaceModule(classSpace, BeanScanning.INDEX));
    modules.add(BeanContainer.module(container));

    Injector injector = Guice.createInjector(new WireModule(modules));

    // TODO: track injector to plugin coordinate for listing/removal

    return null;
  }

  private static URL url(final File file) {
    try {
      return file.toURI().toURL();
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
