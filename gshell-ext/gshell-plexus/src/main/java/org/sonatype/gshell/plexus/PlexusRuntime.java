/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.plexus;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;

/**
 * Provides access to Plexus components.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 3.0
 */
public class PlexusRuntime
{
    private ClassWorld classWorld;

    private PlexusContainer container;

    public PlexusRuntime(final ClassWorld classWorld) {
        this.classWorld = classWorld;
    }

    public PlexusRuntime() {
        this(null);
    }

    private PlexusContainer createContainer() throws PlexusContainerException {
        if (classWorld == null) {
            classWorld = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader());
        }

        ContainerConfiguration config = new DefaultContainerConfiguration()
            .setClassWorld(classWorld)
            .setName("plexus-runtime");

        DefaultPlexusContainer container = new DefaultPlexusContainer(config);
        container.setLoggerManager(new Slf4jLoggerManager());
        container.getLoggerManager().setThresholds(Logger.LEVEL_DEBUG);

        return container;
    }

    public ClassWorld getClassWorld() {
        return classWorld;
    }

    public PlexusContainer getContainer() {
        if (container == null) {
            try {
                container = createContainer();
            }
            catch (PlexusContainerException e) {
                throw new RuntimeException(e);
            }
        }
        return container;
    }

    public <T> T lookup(final Class<T> role) throws ComponentLookupException {
        return getContainer().lookup(role);
    }

    public <T> T lookup(final Class<T> role, final String hint) throws ComponentLookupException {
        return getContainer().lookup(role, hint);
    }
}