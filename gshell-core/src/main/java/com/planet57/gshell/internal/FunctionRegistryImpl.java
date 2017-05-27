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
package com.planet57.gshell.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.functions.FunctionSet;
import com.planet57.gshell.guice.BeanContainer;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.functions.FunctionRegistry;
import com.planet57.gshell.functions.FunctionSetRegisteredEvent;
import com.planet57.gshell.functions.FunctionSetRemovedEvent;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.sonatype.goodies.lifecycle.LifecycleSupport;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Key;

/**
 * Default {@link FunctionRegistry}.
 *
 * @since 3.0
 */
@Named
@Singleton
public class FunctionRegistryImpl
  extends LifecycleSupport
  implements FunctionRegistry
{
  private final BeanContainer container;

  private final EventManager eventManager;

  private final CommandProcessorImpl commandProcessor;

  private boolean discoveryEnabled = true;

  @Inject
  public FunctionRegistryImpl(final BeanContainer container,
                              final EventManager eventManager,
                              final CommandProcessorImpl commandProcessor)
  {
    this.container = checkNotNull(container);
    this.eventManager = checkNotNull(eventManager);
    this.commandProcessor = checkNotNull(commandProcessor);
  }

  @VisibleForTesting
  public void setDiscoveryEnabled(final boolean discoveryEnabled) {
    log.debug("Discovery enabled: {}", discoveryEnabled);
    this.discoveryEnabled = discoveryEnabled;
  }

  @Override
  protected void doStart() throws Exception {
    if (discoveryEnabled) {
      log.debug("Watching for functions");
      container.watch(Key.get(FunctionSet.class), new FunctionsMediator(), this);
    }
  }

  private static class FunctionsMediator
    implements Mediator<Named, FunctionSet, FunctionRegistryImpl>
  {
    @Override
    public void add(final BeanEntry<Named, FunctionSet> entry, final FunctionRegistryImpl watcher) throws Exception {
      watcher.add(entry.getValue());
    }

    @Override
    public void remove(final BeanEntry<Named, FunctionSet> entry, final FunctionRegistryImpl watcher) throws Exception {
      watcher.remove(entry.getValue());
    }
  }

  @Override
  public void add(final FunctionSet functions) {
    checkNotNull(functions);
    log.debug("Add: {}", functions);
    commandProcessor.addFunctions(functions);
    eventManager.publish(new FunctionSetRegisteredEvent(functions));
  }

  @Override
  public void remove(final FunctionSet functions) {
    checkNotNull(functions);
    log.debug("Remove: {}", functions);
    commandProcessor.removeFunctions(functions);
    eventManager.publish(new FunctionSetRemovedEvent(functions));
  }
}
