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

package org.sonatype.gshell.help;

import java.util.ArrayList;
import java.util.List;

/**
 * AND {@link HelpPage} filter aggregator.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AndHelpPageFilter
    implements HelpPageFilter
{
    private final List<HelpPageFilter> filters = new ArrayList<HelpPageFilter>();

    public List<HelpPageFilter> getFilters() {
        return filters;
    }

    public AndHelpPageFilter add(final HelpPageFilter filter) {
        assert filter != null;
        getFilters().add(filter);
        return this;
    }

    public boolean accept(final HelpPage page) {
        assert page != null;
        for (HelpPageFilter filter : getFilters()) {
            if (!filter.accept(page)) {
                return false;
            }
        }
        return true;
    }
}