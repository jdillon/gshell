/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.util.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Support for aggregating filters.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public abstract class AggregateFilter<T>
    implements Filter<T>
{
    private final List<Filter<T>> filters = new ArrayList<Filter<T>>();

    protected AggregateFilter() {
        // empty
    }

    protected AggregateFilter(final Filter<T>... filters) {
        assert filters != null;
        Collections.addAll(getFilters(), filters);
    }

    public List<Filter<T>> getFilters() {
        return filters;
    }

    public AggregateFilter<T> add(final Filter<T>... filters) {
        assert filters != null;
        Collections.addAll(getFilters(), filters);
        return this;
    }

    public AggregateFilter<T> and(final Filter<T>... filters) {
        assert filters != null;
        getFilters().add(new AndFilter<T>(filters));
        return this;
    }

    public AggregateFilter<T> or(final Filter<T>... filters) {
        assert filters != null;
        getFilters().add(new OrFilter<T>(filters));
        return this;
    }

    public AggregateFilter<T> not(final Filter<T> filter) {
        assert filter != null;
        getFilters().add(new NotFilter<T>(filter));
        return this;
    }
}