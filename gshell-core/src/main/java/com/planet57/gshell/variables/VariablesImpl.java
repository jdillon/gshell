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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.util.converter.Converters;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a nested-namespace for command variables.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class VariablesImpl
    implements Variables, EventAware
{
  private final Map<String, Object> map;

  @Nullable
  private final Variables parent;

  private final Set<String> immutables = new HashSet<String>();

  private EventManager eventManager;

  public VariablesImpl(final Map<String, Object> map, @Nullable final Variables parent) {
    this.map = checkNotNull(map);
    this.parent = parent;
  }

  public VariablesImpl(final Variables parent) {
    this(new LinkedHashMap<String, Object>(), parent);
  }

  public VariablesImpl(final Map<String, Object> map) {
    this(map, null);
  }

  public VariablesImpl() {
    this(new LinkedHashMap<String, Object>());
  }

  public void setEventManager(final EventManager eventManager) {
    this.eventManager = checkNotNull(eventManager);
  }

  @Override
  public void set(final String name, final Object value) {
    set(name, value, true);
  }

  @Override
  public void set(final String name, final Object value, boolean mutable) {
    assert name != null;

    ensureMutable(name);

    Object previous = map.put(name, value);

    if (!mutable) {
      immutables.add(name);
    }

    if (eventManager != null) {
      eventManager.publish(new VariableSetEvent(name, previous));
    }
  }

  @Override
  public void set(final Class<?> type, final Object value) {
    assert type != null;

    set(type.getName(), value);
  }

  @Override
  public Object get(final String name) {
    assert name != null;

    Object value = map.get(name);
    if (value == null && parent != null) {
      value = parent.get(name);
    }

    return value;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public <T> T get(final String name, final Class<T> type) {
    assert type != null;

    Object value = get(name);

    if (value != null && !type.isAssignableFrom(value.getClass())) {
      value = Converters.getValue(type, value.toString());
    }

    return (T) value;
  }

  @Override
  public <T> T get(final String name, final Class<T> type, final T defaultValue) {
    T value = get(name, type);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public <T> T get(final Class<T> type, final T defaultValue) {
    assert type != null;

    return get(type.getName(), type, defaultValue);
  }

  @Override
  public <T> T get(final Class<T> type) {
    assert type != null;

    return get(type.getName(), type);
  }

  @Override
  public Object get(final String name, final Object defaultValue) {
    Object value = get(name);
    if (value == null) {
      return defaultValue;
    }

    return value;
  }

  @Override
  public void unset(final String name) {
    assert name != null;

    ensureMutable(name);

    map.remove(name);

    if (eventManager != null) {
      eventManager.publish(new VariableUnsetEvent(name));
    }
  }

  @Override
  public void unset(final Class<?> type) {
    assert type != null;

    unset(type.getName());
  }

  @Override
  public boolean contains(final String name) {
    assert name != null;

    return map.containsKey(name);
  }

  @Override
  public boolean contains(final Class<?> type) {
    assert type != null;

    return contains(type.getName());
  }

  @Override
  public boolean isMutable(final String name) {
    assert name != null;

    boolean mutable = true;

    // First ask out parent if there is one, if they are immutable, then so are we
    if (parent != null) {
      mutable = parent.isMutable(name);
    }

    if (mutable) {
      mutable = !immutables.contains(name);
    }

    return mutable;
  }

  @Override
  public boolean isMutable(final Class<?> type) {
    assert type != null;

    return isMutable(type.getName());
  }

  private void ensureMutable(final String name) {
    assert name != null;

    if (!isMutable(name)) {
      throw new ImmutableVariableException(name);
    }
  }

  @Override
  public boolean isCloaked(final String name) {
    assert name != null;

    int count = 0;

    Variables vars = this;
    while (vars != null && count < 2) {
      if (vars.contains(name)) {
        count++;
      }

      vars = vars.parent();
    }

    return count > 1;
  }

  @Override
  public boolean isCloaked(final Class<?> type) {
    assert type != null;

    return isCloaked(type.getName());
  }

  @Override
  public Iterator<String> names() {
    // Chain to parent iterator if we have a parent
    return new Iterator<String>()
    {
      Iterator<String> iter = map.keySet().iterator();

      boolean more = parent() != null;

      public boolean hasNext() {
        boolean next = iter.hasNext();
        if (!next && more) {
          iter = parent().names();
          more = false;
          next = hasNext();
        }

        return next;
      }

      public String next() {
        return iter.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Variables parent() {
    return parent;
  }
}
