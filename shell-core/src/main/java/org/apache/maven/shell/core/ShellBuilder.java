/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.core;

import org.apache.maven.shell.Shell;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.core.impl.ShellImpl;
import org.apache.maven.shell.core.impl.CommandRegistrationAgent;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.SystemInputOutputHijacker;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds {@link Shell} instances.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellBuilder
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private IO io;

    private Variables variables;

    private PlexusContainer container;

    private boolean registerCommands = true;

    public ShellBuilder setIo(final IO io) {
        this.io = io;
        return this;
    }

    public ShellBuilder setVariables(final Variables variables) {
        this.variables = variables;
        return this;
    }

    public ShellBuilder setContainer(PlexusContainer container) {
        this.container = container;
        return this;
    }

    public ShellBuilder setRegisterCommands(boolean registerCommands) {
        this.registerCommands = registerCommands;
        return this;
    }

    public Shell create() throws Exception {
        if (container == null) {
            // Create the container
            ContainerConfiguration config = new DefaultContainerConfiguration();
            container = new DefaultPlexusContainer(config);
        }

        // Hijack the system output streams
        if (!SystemInputOutputHijacker.isInstalled()) {
            SystemInputOutputHijacker.install();
        }
        
        // Create the shell instance
        ShellImpl shell = (ShellImpl)container.lookup(Shell.class);
        shell.setIo(io != null ? io : new IO());
        shell.setVariables(variables != null ? variables : new Variables());

        // Maybe register default commands
        if (registerCommands) {
            container.lookup(CommandRegistrationAgent.class).registerCommands();
        }

        log.debug("Created shell: {}", shell);
        
        return shell;
    }
}