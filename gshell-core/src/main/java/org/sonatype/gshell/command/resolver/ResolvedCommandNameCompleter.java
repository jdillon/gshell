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

package org.sonatype.gshell.command.resolver;

import com.google.inject.Inject;
import jline.console.Completer;
import jline.console.completers.StringsCompleter;

import java.util.List;

/**
 * {@link Completer} for command names in context.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class ResolvedCommandNameCompleter
    implements Completer
{
    private final CommandResolver resolver;

    private final StringsCompleter delegate = new StringsCompleter();

    @Inject
    public ResolvedCommandNameCompleter(final CommandResolver resolver) {
        assert resolver != null;
        this.resolver = resolver;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // Have to rebuild every time :-(
        delegate.getStrings().clear();

        Node node = null;
        if (buffer != null) {
            node = resolver.resolve(buffer.substring(0, cursor));
        }
        if (node == null) {
            node = resolver.group();
        }

        if (node.isGroup()) {
            for (Node child : node.children()) {
                delegate.getStrings().add(child.getName());
            }
        }
        else {
            delegate.getStrings().add(node.getName());
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}