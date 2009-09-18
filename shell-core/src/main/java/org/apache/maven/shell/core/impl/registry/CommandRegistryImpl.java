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

package org.apache.maven.shell.core.impl.registry;

import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.NameAware;
import org.apache.maven.shell.event.EventManager;
import org.apache.maven.shell.registry.CommandRegistry;
import org.apache.maven.shell.registry.DuplicateCommandException;
import org.apache.maven.shell.registry.NoSuchCommandException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The default {@link CommandRegistry} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandRegistry.class)
public class CommandRegistryImpl
    implements CommandRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Set<String> commands = new LinkedHashSet<String>();

    @Requirement
    private PlexusContainer container;

    @Requirement
    private EventManager eventManager;

    public void registerCommand(final String name) throws DuplicateCommandException {
        assert name != null;

        log.debug("Registering command: {}", name);

        if (containsCommand(name)) {
            throw new DuplicateCommandException(name);
        }
        
        commands.add(name);

        eventManager.publish(new CommandRegisteredEvent(name));
    }

    public void removeCommand(final String name) throws NoSuchCommandException {
        assert name != null;

        log.trace("Removing command: {}", name);

        if (!containsCommand(name)) {
            throw new NoSuchCommandException(name);
        }

        commands.remove(name);

        eventManager.publish(new CommandRemovedEvent(name));
    }

    public Command getCommand(final String name) throws NoSuchCommandException {
        assert name != null;

        log.trace("Getting command: {}", name);

        if (!containsCommand(name)) {
            throw new NoSuchCommandException(name);
        }

        Command command;
        try {
            command = container.lookup(Command.class, name);
        }
        catch (ComponentLookupException e) {
            throw new NoSuchCommandException(name);
        }

        // Inject the name of the command
        if (command instanceof NameAware) {
            ((NameAware)command).setName(name);
        }

        return command;
    }

    public boolean containsCommand(final String name) {
        assert name != null;

        return commands.contains(name);
    }

    public Collection<String> getCommandNames() {
        return Collections.unmodifiableSet(commands);
    }
}