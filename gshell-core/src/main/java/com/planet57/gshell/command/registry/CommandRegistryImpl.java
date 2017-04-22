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
package com.planet57.gshell.command.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.event.EventManager;
import org.sonatype.goodies.common.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandRegistry} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class CommandRegistryImpl
  extends ComponentSupport
  implements CommandRegistry
{
  private final Map<String, CommandAction> commands = new LinkedHashMap<>();

  private final EventManager events;

  @Inject
  public CommandRegistryImpl(final EventManager events) {
    this.events = checkNotNull(events);
  }

  @Override
  public void registerCommand(final String name, final CommandAction command) throws DuplicateCommandException {
    checkNotNull(name);

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
    if (command instanceof CommandAction.NameAware) {
      ((CommandAction.NameAware) command).setName(name);
    }

    commands.put(name, command);
    events.publish(new CommandRegisteredEvent(name, command));
  }

  @Override
  public void removeCommand(final String name) throws NoSuchCommandException {
    checkNotNull(name);

    log.trace("Removing command: {}", name);

    if (!containsCommand(name)) {
      throw new NoSuchCommandException(name);
    }

    commands.remove(name);
    events.publish(new CommandRemovedEvent(name));
  }

  @Override
  public CommandAction getCommand(final String name) throws NoSuchCommandException {
    checkNotNull(name);

    if (!containsCommand(name)) {
      throw new NoSuchCommandException(name);
    }
    return commands.get(name);
  }

  @Override
  public boolean containsCommand(final String name) {
    checkNotNull(name);

    return commands.containsKey(name);
  }

  @Override
  public Collection<String> getCommandNames() {
    return Collections.unmodifiableSet(commands.keySet());
  }

  @Override
  public Collection<CommandAction> getCommands() {
    return Collections.unmodifiableCollection(commands.values());
  }
}
