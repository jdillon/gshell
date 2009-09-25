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

package org.apache.gshell.core.impl.registry;

import org.apache.gshell.command.Command;
import org.apache.gshell.command.NameAware;
import org.apache.gshell.event.EventManager;
import org.apache.gshell.registry.CommandRegistry;
import org.apache.gshell.registry.DuplicateCommandException;
import org.apache.gshell.registry.NoSuchCommandException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The default {@link org.apache.gshell.registry.CommandRegistry} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
@Component(role= CommandRegistry.class)
public class CommandRegistryImpl
    implements CommandRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Command> commands = new LinkedHashMap<String,Command>();

    @Requirement
    private EventManager eventManager;

    public CommandRegistryImpl() {}

    public CommandRegistryImpl(final EventManager eventManager) {
        assert eventManager != null;
        this.eventManager = eventManager;
    }

    public void registerCommand(final String name, final Command command) throws DuplicateCommandException {
        assert name != null;

        if (log.isTraceEnabled()) {
            log.trace("Registering command: {} -> {}", name, command);
        }
        else {
            log.trace("Registering command: {}", name);    
        }

        if (containsCommand(name)) {
            throw new DuplicateCommandException(name);
        }

        // Inject the name of the command
        if (command instanceof NameAware) {
            ((NameAware)command).setName(name);
        }
        
        commands.put(name, command);

        eventManager.publish(new CommandRegisteredEvent(name, command));
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

        return commands.get(name);
    }

    public boolean containsCommand(final String name) {
        assert name != null;

        return commands.containsKey(name);
    }

    public Collection<String> getCommandNames() {
        return Collections.unmodifiableSet(commands.keySet());
    }
}