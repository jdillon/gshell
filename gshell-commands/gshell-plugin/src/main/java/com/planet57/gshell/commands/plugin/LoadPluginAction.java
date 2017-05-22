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
import com.planet57.gshell.commands.plugin.internal.PluginManager;
import com.planet57.gshell.commands.plugin.internal.PluginRegistration;
import com.planet57.gshell.guice.BeanContainer;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.cli2.Option;
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

  @Inject
  private PluginManager pluginManager;

  @Argument(required = true, description = "Plugin artifact coordinates", token = "COORD")
  private String coordinates;

  @Option(name="o", longName = "offline")
  private boolean offline = false;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    DefaultRepositorySystemSession session = repositoryAccess.createSession();
    session.setTransferListener(new TerminalTransferListener(context.getIo()));

    session.setOffline(offline);

    Artifact artifact = new DefaultArtifact(coordinates);
    Dependency dependency = new Dependency(artifact, null);

    // TODO: need to figure out how to effectively exclude gshell-core and related bits provided by the runtime which would be invalid to duplicate

    CollectRequest collectRequest = new CollectRequest(dependency, repositoryAccess.getRemoteRepositories());
    DependencyRequest request = new DependencyRequest(collectRequest, null);

    log.debug("Resolving dependencies: {}", dependency);
    DependencyResult result = repositoryAccess.getRepositorySystem().resolveDependencies(session, request);

    // build class-path for plugin
    URL[] classPath = result.getArtifactResults().stream()
      .map(artifactResult -> url(artifactResult.getArtifact().getFile()))
      .toArray(URL[]::new);

    if (log.isDebugEnabled()) {
      log.debug("Class-path:");
      for (URL url : classPath) {
        log.debug("  {}", url);
      }
    }

    // TODO: detect which class-path members have components to optimize class-space
    URLClassLoader cl = new URLClassLoader(classPath, Shell.class.getClassLoader());
    URLClassSpace classSpace = new URLClassSpace(cl, classPath);

    Injector injector = Guice.createInjector(new WireModule(
      new SpaceModule(classSpace, BeanScanning.INDEX),
      BeanContainer.module(container)
    ));
    // injector is automatically bound to BeanLocator by sisu

    PluginRegistration registration = new PluginRegistration(artifact, cl, injector);
    pluginManager.add(registration);

    context.getIo().format("Plugin loaded: %s%n", registration.getId());

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
