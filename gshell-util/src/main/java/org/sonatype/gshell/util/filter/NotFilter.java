/*
 * Copyright (C) 2010 the original author or authors.
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

/**
 * Not filter.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class NotFilter<T>
    implements Filter<T>
{
    private final Filter<T> filter;

    public NotFilter(final Filter<T> filter) {
        assert filter != null;
        this.filter = filter;
    }

    public Filter<T> getFilter() {
        return filter;
    }

    public boolean accept(final T value) {
        return !filter.accept(value);
    }
}