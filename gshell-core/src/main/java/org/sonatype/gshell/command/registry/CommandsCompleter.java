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
package org.sonatype.gshell.command.registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import jline.console.completer.NullCompleter;
import jline.console.completer.Completer;
import jline.console.completer.ArgumentCompleter;
import org.sonatype.gshell.command.resolver.CommandResolver;
import org.sonatype.gshell.command.resolver.Node;
import org.sonatype.gshell.command.resolver.NodePathCompleter;

import java.util.Collection;
import java.util.List;

/**
 * Command path and argument completer.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class CommandsCompleter
    extends ArgumentCompleter
{
    private final PathCompleter first;

    @Inject
    public CommandsCompleter(final CommandResolver resolver) {
        first = new PathCompleter(resolver);
        getCompleters().add(first);
    }

    private class PathCompleter
        extends NodePathCompleter
    {
        private PathCompleter(final CommandResolver resolver) {
            super(resolver);
        }

        @Override
        protected int buildCandidates(final List<CharSequence> candidates, final Collection<Node> matches, final String prefix) {
            assert matches != null;

            // If there is only one match, then install that node's action completers
            if (matches.size() == 1) {
                updateCompleters(matches.iterator().next().getAction().getCompleters());
            }

            return super.buildCandidates(candidates, matches, prefix);
        }
    }

    private void updateCompleters(final Completer[] completers) {
        // completers may be null

        // Reset the list
        List<Completer> target = getCompleters();
        target.clear();
        target.add(first);

        // Install new child completers
        if (completers != null) {
            for (Completer completer : completers) {
                target.add(completer != null ? completer : NullCompleter.INSTANCE);
            }
        }
        else {
            target.add(NullCompleter.INSTANCE);
        }
    }
}