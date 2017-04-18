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

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The default {@link EventManager} components.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class EventManagerImpl
    implements EventManager
{
  private static final Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

  private final EventBus eventBus;

  public EventManagerImpl() {
    this.eventBus = new EventBus();
  }

  @Override
  public void addListener(final Object listener) {
    checkArgument(listener != null);

    log.trace("Adding listener: {}", listener);

    eventBus.register(listener);
  }

  @Override
  public void removeListener(final Object listener) {
    checkArgument(listener != null);

    log.trace("Removing listener: {}", listener);

    try {
      eventBus.unregister(listener);
    }
    catch (IllegalArgumentException e) {
      // FIXME: this is to cope with previous impls semantics that ignored remove for unregistered, which guava eventbus does not allow
    }
  }

  @Override
  public void publish(final Object event) {
    checkArgument(event != null);

    log.trace("Publishing event: {}", event);
    eventBus.post(event);
  }
}
