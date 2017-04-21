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
package com.planet57.gshell.alias;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.util.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AliasRegistry} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class AliasRegistryImpl
  extends ComponentSupport
  implements AliasRegistry
{
  private final Map<String, String> aliases = new LinkedHashMap<>();

  private final EventManager events;

  @Inject
  public AliasRegistryImpl(final EventManager events) {
    this.events = checkNotNull(events);
  }

  @Override
  public void registerAlias(final String name, final String alias) {
    checkNotNull(name);
    checkNotNull(alias);

    log.debug("Registering alias: {} -> {}", name, alias);

    if (log.isDebugEnabled()) {
      if (containsAlias(name)) {
        log.debug("Replacing alias: {}", name);
      }
    }

    aliases.put(name, alias);

    events.publish(new AliasRegisteredEvent(name, alias));
  }

  @Override
  public void removeAlias(final String name) throws NoSuchAliasException {
    checkNotNull(name);

    log.debug("Removing alias: {}", name);

    if (!containsAlias(name)) {
      throw new NoSuchAliasException(name);
    }

    aliases.remove(name);

    events.publish(new AliasRemovedEvent(name));
  }

  @Override
  public String getAlias(final String name) throws NoSuchAliasException {
    checkNotNull(name);

    if (!containsAlias(name)) {
      throw new NoSuchAliasException(name);
    }

    return aliases.get(name);
  }

  @Override
  public boolean containsAlias(final String name) {
    checkNotNull(name);

    return aliases.containsKey(name);
  }

  @Override
  public Map<String, String> getAliases() {
    return Collections.unmodifiableMap(aliases);
  }
}
