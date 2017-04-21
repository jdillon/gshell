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
package com.planet57.gshell.command.registry;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.command.resolver.NodePathCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;

/**
 * Command path and argument completer.
 *
 * This will resolve the qualified path to the command as well as the commands options and arguments as configured.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("command")
@Singleton
public class CommandCompleter
    extends ArgumentCompleter
{
  private final PathCompleter first;

  @Inject
  public CommandCompleter(final CommandResolver resolver) {
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
    protected void buildCandidates(final List<Candidate> candidates,
                                   final Collection<Node> matches,
                                   final String prefix)
    {
      assert matches != null;

      // If there is only one match, then install that node's action completers
      if (matches.size() == 1) {
        updateCompleters(matches.iterator().next().getAction().getCompleters());
      }

      super.buildCandidates(candidates, matches, prefix);
    }
  }

  private void updateCompleters(@Nullable final Completer[] completers) {
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
