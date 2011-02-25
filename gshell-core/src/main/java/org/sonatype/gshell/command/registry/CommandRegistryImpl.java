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
package org.sonatype.gshell.command.registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.util.NameAware;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link CommandRegistry} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class CommandRegistryImpl
    implements CommandRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, CommandAction> commands = new LinkedHashMap<String, CommandAction>();

    private final EventManager events;

    @Inject
    public CommandRegistryImpl(final EventManager events) {
        assert events != null;
        this.events = events;
    }

    public void registerCommand(final String name, final CommandAction command) throws DuplicateCommandException {
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
            ((NameAware) command).setName(name);
        }

        commands.put(name, command);
        events.publish(new CommandRegisteredEvent(name, command));
    }

    public void removeCommand(final String name) throws NoSuchCommandException {
        assert name != null;

        log.trace("Removing command: {}", name);

        if (!containsCommand(name)) {
            throw new NoSuchCommandException(name);
        }

        commands.remove(name);
        events.publish(new CommandRemovedEvent(name));
    }

    public CommandAction getCommand(final String name) throws NoSuchCommandException {
        assert name != null;
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

    public Collection<CommandAction> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }
}