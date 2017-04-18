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

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Key;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandAction;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class CommandRegistrarImpl
    implements CommandRegistrar
{
  private static final Logger log = LoggerFactory.getLogger(CommandRegistrarImpl.class);

  private final MutableBeanLocator container;

  private final CommandRegistry registry;

  @Inject
  public CommandRegistrarImpl(final MutableBeanLocator container,
                              final CommandRegistry registry)
  {
    this.container = checkNotNull(container);
    this.registry = checkNotNull(registry);
  }

  private Class<?> loadClass(final String className) throws ClassNotFoundException {
    assert className != null;
    return Thread.currentThread().getContextClassLoader().loadClass(className);
  }

  @Override
  public void discoverCommands() throws Exception {
    log.trace("Registering commands");

    for (BeanEntry<?,CommandAction> entry : container.locate(Key.get(CommandAction.class, Command.class))) {
      log.trace("Registering command: {}", entry);
      Command command = (Command) entry.getKey();
      CommandAction action = entry.getValue();
      registry.registerCommand(command.name(), action);
    }
  }

  @Override
  public void registerCommand(final String name, final String className) throws Exception {
    assert name != null;
    assert className != null;

    log.trace("Registering command: {} -> {}", name, className);

    CommandAction command = createAction(className);
    registry.registerCommand(name, command);
  }

  @Override
  public void registerCommand(final String className) throws Exception {
    assert className != null;

    log.trace("Registering command: {}", className);

    CommandAction command = createAction(className);

    Command meta = command.getClass().getAnnotation(Command.class);
    assert meta != null;
    String name = meta.name();

    registry.registerCommand(name, command);
  }

  @SuppressWarnings({"unchecked"})
  private CommandAction createAction(final String className) throws ClassNotFoundException {
    assert className != null;
    Class<?> type = loadClass(className);
    Iterator<BeanEntry<Annotation, ?>> iter = container.locate(Key.get((Class) type)).iterator();
    if (iter.hasNext()) {
      return (CommandAction) iter.next().getValue();
    }
    // This should really never happen
    throw new RuntimeException("Unable to load command action implementation: " + type);
  }
}
