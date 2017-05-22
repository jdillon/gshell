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
package com.planet57.gshell.command;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Key;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.internal.BeanContainer;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.sonatype.goodies.lifecycle.LifecycleSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CommandRegistry}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class CommandRegistryImpl
  extends LifecycleSupport
  implements CommandRegistry
{
  private final BeanContainer container;

  private final EventManager events;

  private final Map<String, CommandAction> commands = new LinkedHashMap<>();

  private boolean discoveryEnabled = true;

  @Inject
  public CommandRegistryImpl(final BeanContainer container,
                             final EventManager events)
  {
    this.container = checkNotNull(container);
    this.events = checkNotNull(events);
  }

  @VisibleForTesting
  public void setDiscoveryEnabled(final boolean discoveryEnabled) {
    log.debug("Discovery enabled: {}", discoveryEnabled);
    this.discoveryEnabled = discoveryEnabled;
  }

  @Override
  protected void doStart() throws Exception {
    if (discoveryEnabled) {
      log.debug("Watching for commands");
      container.watch(Key.get(CommandAction.class, Command.class), new CommandMediator(), this);
    }
  }

  private static class CommandMediator
    implements Mediator<Command, CommandAction, CommandRegistryImpl>
  {
    @Override
    public void add(final BeanEntry<Command, CommandAction> entry, final CommandRegistryImpl watcher) throws Exception {
      watcher.registerCommand(entry.getKey().name(), entry.getValue());
    }

    @Override
    public void remove(final BeanEntry<Command, CommandAction> entry, final CommandRegistryImpl watcher) throws Exception {
      watcher.removeCommand(entry.getKey().name());
    }
  }

  @Override
  public void registerCommand(final String name, final CommandAction command) throws DuplicateCommandException {
    checkNotNull(name);

    // provide configured command name to action if requested
    if (command instanceof CommandAction.NameAware) {
      ((CommandAction.NameAware) command).setName(name);
    }

    if (log.isTraceEnabled()) {
      log.trace("Registering command: {} -> {}", name, command);
    }
    else {
      log.trace("Registering command: {}", name);
    }

    if (containsCommand(name)) {
      throw new DuplicateCommandException(name);
    }

    commands.put(name, command);
    events.publish(new CommandRegisteredEvent(name, command));
  }

  @VisibleForTesting
  public void registerCommand(final String name, final Class type) throws Exception {
    checkNotNull(name);
    checkNotNull(type);

    CommandAction action = createAction(type);
    registerCommand(name, action);
  }

  @SuppressWarnings({"unchecked"})
  private CommandAction createAction(final Class<?> type) throws ClassNotFoundException {
    Iterator<BeanEntry<Annotation, ?>> iter = container.locate((Class)type).iterator();
    if (iter.hasNext()) {
      return (CommandAction) iter.next().getValue();
    }
    // This should really never happen
    throw new RuntimeException("Unable to load command action implementation: " + type);
  }

  @Override
  public void removeCommand(final String name) throws NoSuchCommandException {
    checkNotNull(name);

    log.trace("Removing command: {}", name);

    CommandAction action = commands.remove(name);
    if (action == null) {
      throw new NoSuchCommandException(name);
    }

    events.publish(new CommandRemovedEvent(name));
  }

  @Override
  public CommandAction getCommand(final String name) throws NoSuchCommandException {
    checkNotNull(name);

    CommandAction action = commands.get(name);
    if (action == null) {
      throw new NoSuchCommandException(name);
    }
    return action;
  }

  @Override
  public boolean containsCommand(final String name) {
    checkNotNull(name);

    return commands.containsKey(name);
  }

  @Override
  public Collection<CommandAction> getCommands() {
    return Collections.unmodifiableCollection(commands.values());
  }
}
