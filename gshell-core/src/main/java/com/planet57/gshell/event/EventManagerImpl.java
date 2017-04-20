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
package com.planet57.gshell.event;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import com.google.inject.Key;
import com.planet57.gshell.guice.BeanContainer;
import com.planet57.gshell.util.ComponentSupport;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link EventManager} component.
 *
 * This is now a thin adapter around a Guava {@link EventBus}.
 *
 * Supports automatically calling {@link #register(Object)} for {@link EventAware} components.
 *
 * @since 2.0
 */
@Named
@Singleton
public class EventManagerImpl
  extends ComponentSupport
  implements EventManager
{
  private final BeanContainer container;

  private final EventBus eventBus;

  @Inject
  public EventManagerImpl(final BeanContainer container) {
    this.container = checkNotNull(container);
    this.eventBus = new EventBus();
  }

  // HACK: really need some component lifecycle

  /**
   * Automatically register/unregister event-aware components.
   */
  @Override
  public void start() {
    container.watch(Key.get(EventAware.class, Named.class), new EventAwareMediator(), this);
  }

  private static class EventAwareMediator
    implements Mediator<Named, EventAware, EventManagerImpl>
  {
    @Override
    public void add(final BeanEntry<Named, EventAware> entry, final EventManagerImpl watcher) {
      watcher.register(entry.getValue());
    }

    @Override
    public void remove(final BeanEntry<Named, EventAware> entry, final EventManagerImpl watcher) {
      watcher.unregister(entry.getValue());
    }
  }

  @Override
  public void register(final Object listener) {
    checkNotNull(listener);
    log.trace("Adding listener: {}", listener);
    eventBus.register(listener);
  }

  @Override
  public void unregister(final Object listener) {
    checkNotNull(listener);
    log.trace("Removing listener: {}", listener);
    eventBus.unregister(listener);
  }

  @Override
  public void publish(final Object event) {
    checkNotNull(event);
    log.trace("Publishing event: {}", event);
    eventBus.post(event);
  }
}
