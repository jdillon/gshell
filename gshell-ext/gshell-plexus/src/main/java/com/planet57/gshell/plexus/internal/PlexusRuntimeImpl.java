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
package com.planet57.gshell.plexus.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.planet57.gshell.plexus.PlexusRuntime;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.goodies.common.ComponentSupport;

/**
 * Default {@link PlexusRuntime}.
 *
 * @since 3.0
 */
@Named
@Singleton
public class PlexusRuntimeImpl
  extends ComponentSupport
  implements PlexusRuntime
{
  private final Provider<ClassWorld> classWorld;

  @Nullable
  private PlexusContainer container;

  @Inject
  public PlexusRuntimeImpl(final Provider<ClassWorld> classWorld) {
    this.classWorld = checkNotNull(classWorld);
  }

  // TODO: consider exposing container for provider-based customization?

  private PlexusContainer createContainer() throws PlexusContainerException {
    ContainerConfiguration config = new DefaultContainerConfiguration()
        .setClassWorld(classWorld.get())
        .setName("plexus-runtime");

    DefaultPlexusContainer container = new DefaultPlexusContainer(config);
    container.setLoggerManager(new Slf4jLoggerManager());
    container.getLoggerManager().setThresholds(Logger.LEVEL_DEBUG);

    return container;
  }

  @Override
  public PlexusContainer getContainer() {
    if (container == null) {
      try {
        container = createContainer();
        log.debug("Created container: {}", container);
      }
      catch (PlexusContainerException e) {
        throw new RuntimeException(e);
      }
    }
    return container;
  }

  @Override
  public <T> T lookup(final Class<T> role) throws ComponentLookupException {
    checkNotNull(role);
    return getContainer().lookup(role);
  }

  @Override
  public <T> T lookup(final Class<T> role, final String hint) throws ComponentLookupException {
    checkNotNull(role);
    checkNotNull(hint);
    return getContainer().lookup(role, hint);
  }
}
