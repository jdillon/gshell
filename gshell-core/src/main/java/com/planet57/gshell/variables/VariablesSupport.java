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
package com.planet57.gshell.variables;

import java.util.LinkedHashMap;
import java.util.Map;

import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.util.converter.Converters;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Default {@link Variables}.
 *
 * @since 2.0
 */
public class VariablesSupport
    implements Variables, EventAware
{
  private final Map<String, Object> map;

  private EventManager eventManager;

  public VariablesSupport(final Map<String, Object> map) {
    this.map = checkNotNull(map);
  }

  public VariablesSupport() {
    this(new LinkedHashMap<>());
  }

  /**
   * Informs variables to become event-aware and fire {@link VariableSetEvent} and {@link VariableUnsetEvent}.
   */
  @Inject
  public void setEventManager(final EventManager eventManager) {
    this.eventManager = checkNotNull(eventManager);
  }

  @Override
  public void set(final String name, @Nullable final Object value) {
    set(name, value, true);
  }

  @Override
  public void set(final String name, @Nullable final Object value, boolean mutable) {
    checkNotNull(name);

    Object previous = map.put(name, value);

    if (eventManager != null) {
      eventManager.publish(new VariableSetEvent(name, previous));
    }
  }

  @Override
  public void set(final Class<?> type, @Nullable final Object value) {
    checkNotNull(type);
    set(type.getName(), value);
  }

  @Override
  @Nullable
  public Object get(final String name) {
    checkNotNull(name);
    return map.get(name);
  }

  @Override
  @Nullable
  @SuppressWarnings({"unchecked"})
  public <T> T get(final String name, final Class<T> type) {
    checkNotNull(type);
    Object value = get(name);
    if (value != null && !type.isAssignableFrom(value.getClass())) {
      value = Converters.getValue(type, value.toString());
    }
    return (T) value;
  }

  @Override
  @Nullable
  public <T> T get(final String name, final Class<T> type, @Nullable final T defaultValue) {
    T value = get(name, type);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  @Nullable
  public <T> T get(final Class<T> type, @Nullable final T defaultValue) {
    checkNotNull(type);
    return get(type.getName(), type, defaultValue);
  }

  @Override
  @Nullable
  public <T> T get(final Class<T> type) {
    checkNotNull(type);
    return get(type.getName(), type);
  }

  @Override
  @Nullable
  public Object get(final String name, @Nullable final Object defaultValue) {
    Object value = get(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public Object require(final String name) {
    Object result = get(name);
    checkState(result != null, "Missing variable: %s", name);
    return result;
  }

  @Override
  public Object require(final String name, final Object defaultValue) {
    checkNotNull(defaultValue);
    Object result = get(name, defaultValue);
    checkState(result != null, "Missing variable: %s", name);
    return result;
  }

  @Override
  public <T> T require(final String name, final Class<T> type, final T defaultValue) {
    checkNotNull(defaultValue);
    T result = get(name, type, defaultValue);
    checkState(result != null, "Missing variable: %s", name);
    return result;
  }

  @Override
  public <T> T require(final String name, final Class<T> type) {
    T result = get(name, type);
    checkState(result != null, "Missing variable: %s", name);
    return result;
  }

  @Override
  public <T> T require(final Class<T> type, final T defaultValue) {
    checkNotNull(defaultValue);
    T result = get(type, defaultValue);
    checkState(result != null, "Missing variable: %s", type.getName());
    return result;
  }

  @Override
  public <T> T require(final Class<T> type) {
    T result = get(type);
    checkState(result != null, "Missing variable: %s", type.getName());
    return result;
  }

  @Override
  public void unset(final String name) {
    checkNotNull(name);
    map.remove(name);

    if (eventManager != null) {
      eventManager.publish(new VariableUnsetEvent(name));
    }
  }

  @Override
  public void unset(final Class<?> type) {
    checkNotNull(type);
    unset(type.getName());
  }

  @Override
  public boolean contains(final String name) {
    checkNotNull(name);
    return map.containsKey(name);
  }

  @Override
  public boolean contains(final Class<?> type) {
    checkNotNull(type);
    return contains(type.getName());
  }

  @Override
  public Iterable<String> names() {
    return map.keySet();
  }

  @Override
  public Map<String,Object> asMap() {
    return map;
  }
}
