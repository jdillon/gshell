/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides a nested-namespace for command variables.
 *
 * @version $Rev$ $Date$
 */
public class Variables
{
    private final Map<String,Object> map;

    private final Variables parent;

    private final Set<String> immutables = new HashSet<String>();

    public Variables(final Map<String,Object> map, final Variables parent) {
        assert map != null;
        assert parent != null;

        this.map = map;
        this.parent = parent;
    }

    public Variables(final Variables parent) {
        assert parent != null;

        this.map = new HashMap<String,Object>();
        this.parent = parent;
    }

    public Variables(final Map<String,Object> map) {
        assert map != null;

        this.map = map;
        this.parent = null;
    }

    public Variables() {
        this(new HashMap<String,Object>());
    }

    public void set(final String name, final Object value) {
        set(name, value, true);
    }

    public void set(final String name, final Object value, boolean mutable) {
        assert name != null;

        ensureMutable(name);

        map.put(name, value);

        if (!mutable) {
            immutables.add(name);
        }
    }

    public Object get(final String name) {
        assert name != null;

        Object value = map.get(name);
        if (value == null && parent != null) {
            value = parent.get(name);
        }

        return value;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(final String name, final Class<T> type) {
        assert type != null;

        return (T)get(name);
    }
    
    public Object get(final String name, final Object _default) {
        Object value = get(name);
        if (value == null) {
            return _default;
        }

        return value;
    }

    public void unset(final String name) {
        assert name != null;

        ensureMutable(name);

        map.remove(name);
    }

    public boolean contains(final String name) {
        assert name != null;

        return map.containsKey(name);
    }

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

    private void ensureMutable(final String name) {
        assert name != null;

        if (!isMutable(name)) {
            throw new ImmutableVariableException(name);
        }
    }

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

    public Iterator<String> names() {
        // Chain to parent iterator if we have a parent
        return new Iterator<String>() {
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

    public Variables parent() {
        return parent;
    }

    /**
     * Throw to indicate that a variable change was attempted but the variable was not muable.
     */
    public class ImmutableVariableException
        extends RuntimeException
    {
        public ImmutableVariableException(final String name) {
            super("Variable is immutable: " + name);
        }
    }
}
