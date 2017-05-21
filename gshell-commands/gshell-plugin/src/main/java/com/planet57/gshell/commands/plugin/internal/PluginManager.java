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
package com.planet57.gshell.commands.plugin.internal;

import com.google.common.collect.ImmutableList;
import org.sonatype.goodies.common.ComponentSupport;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ???
 *
 * @since 3.0
 */
@Named
@Singleton
public class PluginManager
  extends ComponentSupport
{
  private final Map<String, PluginRegistration> registrations = new ConcurrentHashMap<>();

  public void add(final PluginRegistration registration) {
    checkNotNull(registration);
    log.debug("Add: {}", registration);
    registrations.put(registration.getId(), registration);
  }

  @Nullable
  public PluginRegistration get(final String id) {
    checkNotNull(id);
    return registrations.get(id);
  }

  @Nullable
  public PluginRegistration remove(final String id) {
    checkNotNull(id);
    log.debug("Remove: {}", id);
    return registrations.remove(id);
  }

  public Collection<PluginRegistration> registrations() {
    return ImmutableList.copyOf(registrations.values());
  }
}
