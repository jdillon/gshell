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

package org.apache.gshell.core;

import org.apache.gshell.command.Command;
import org.apache.gshell.registry.CommandRegistrar;
import org.apache.gshell.registry.CommandRegistrarSupport;
import org.apache.gshell.registry.CommandRegistry;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Default implementation of the {@link org.apache.gshell.registry.CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandRegistrar.class)
public class PlexusCommandRegistrar
    extends CommandRegistrarSupport
{
    @Requirement
    private PlexusContainer container;

    @Requirement
    private CommandRegistry registry;

    public PlexusCommandRegistrar() {}

    public PlexusCommandRegistrar(final PlexusContainer container, final CommandRegistry registry) {
        assert container != null;
        assert registry != null;
        this.container = container;
        this.registry = registry;
    }

    public void registerCommand(final String name, final String type) throws Exception {
        assert name != null;
        assert type != null;

        log.trace("Registering command: {} -> {}", name, type);

        Command command = (Command) container.lookup(type);
        registry.registerCommand(name, command);
    }
}