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
package com.planet57.gshell.util.style;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.planet57.gossip.Log;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * In-memory {@link StyleSource}.
 *
 * @since 3.0
 */
public class MemoryStyleSource
  implements StyleSource
{
  private static final Logger log = Log.getLogger(MemoryStyleSource.class);

  private final Map<String,Map<String,String>> backing = new ConcurrentHashMap<>();

  @Nullable
  @Override
  public String get(final String group, final String name) {
    String style = null;
    Map<String,String> styles = backing.get(group);
    if (styles != null) {
      style = styles.get(name);
    }
    log.trace("Get: [{}] {} -> {}", group, name, style);
    return style;
  }

  @Override
  public void set(final String group, final String name, final String style) {
    checkNotNull(group);
    checkNotNull(name);
    checkNotNull(style);
    backing.computeIfAbsent(group, k -> new ConcurrentHashMap<>()).put(name, style);
    log.trace("Set: [{}] {} -> {}", group, name, style);
  }

  @Override
  public void remove(final String group) {
    checkNotNull(group);
    if (backing.remove(group) != null) {
      log.debug("Removed: [{}]");
    }
  }

  @Override
  public void remove(final String group, final String name) {
    checkNotNull(group);
    checkNotNull(name);
    Map<String,String> styles = backing.get(group);
    if (styles != null) {
      styles.remove(name);
      log.debug("Removed: [{}] {}", group, name);
    }
  }

  @Override
  public void clear() {
    backing.clear();
    log.trace("Cleared");
  }

  @Override
  public Iterable<String> groups() {
    return ImmutableSet.copyOf(backing.keySet());
  }

  @Override
  public Map<String,String> styles(final String group) {
    checkNotNull(group);
    Map<String,String> result = backing.get(group);
    if (result == null) {
      result = Collections.emptyMap();
    }
    return ImmutableMap.copyOf(result);
  }
}
