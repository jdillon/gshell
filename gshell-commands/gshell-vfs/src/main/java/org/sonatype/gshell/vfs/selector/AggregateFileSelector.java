/**
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
package org.sonatype.gshell.vfs.selector;

import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Apply multiple selectors.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class AggregateFileSelector
    implements FileSelector
{
    private final Collection<FileSelector> selectors;

    public AggregateFileSelector(final Collection<FileSelector> selectors) {
        assert selectors != null;

        this.selectors = selectors;
    }

    public AggregateFileSelector() {
        this(new ArrayList<FileSelector>());
    }

    public Collection<FileSelector> getSelectors() {
        return selectors;
    }

    public boolean includeFile(final FileSelectInfo selection) throws Exception {
        boolean include = true;

        for (FileSelector selector : selectors) {
            if (!selector.includeFile(selection)) {
                include = false;
                break;
            }
        }

        return include;
    }

    public boolean traverseDescendents(final FileSelectInfo selection) throws Exception {
        boolean include = true;

        for (FileSelector selector : selectors) {
            if (!selector.traverseDescendents(selection)) {
                include = false;
                break;
            }
        }

        return include;
    }
}