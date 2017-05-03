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
package com.planet57.gshell.command.resolver;

import org.sonatype.goodies.common.ComponentSupport;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.command.resolver.Node.SEPARATOR;
import static com.planet57.gshell.util.jline.Candidates.candidate;

/**
 * {@link Completer} for node path names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("node-path")
@Singleton
public class NodePathCompleter
  extends ComponentSupport
  implements Completer
{
  private final CommandResolver resolver;

  @Inject
  public NodePathCompleter(final CommandResolver resolver) {
    this.resolver = checkNotNull(resolver);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
    checkNotNull(candidates);

    Collection<Node> matches = new LinkedList<>();

    String word = line.word();
    log.trace("Completing; {}; word: {}, line: {}, words: {}", line, word, line.line(), line.words());

    // TODO: handle ./, ../ and / resolution

    resolver.searchPath().forEach(parent -> matches.addAll(parent.children()));

    buildCandidates(candidates, matches);
  }

  private void buildCandidates(final List<Candidate> candidates, final Collection<Node> matches) {
    for (Node node : matches) {
      log.trace("Matched: {}", node);
    }

    // append all matching nodes
    matches.forEach(node -> appendNode(candidates, node));

    log.trace("Candidates: {}", candidates);
  }

  private static void appendNode(final Collection<Candidate> candidates, final Node node) {
    if (node.isRoot()) {
      appendChildren(candidates, node);
    }
    else if (node.isGroup()) {
      String name = node.getName() + SEPARATOR;
      candidates.add(candidate(name, node.getDescription()));
      appendChildren(candidates, node);
    }
    else {
      candidates.add(candidate(node.getName(), node.getDescription()));
    }
  }

  private static void appendChildren(final Collection<Candidate> candidates, final Node parent) {
    assert parent.isGroup();

    // prefix children with ${parent.name} + SEPARATOR; unless root
    String prefix = parent.isRoot() ? "" : parent.getName() + SEPARATOR;

    parent.children().forEach(child -> {
      if (child.isGroup()) {
        candidates.add(candidate(prefix + child.getName() + SEPARATOR, child.getDescription()));
        appendChildren(candidates, child);
      }
      else {
        candidates.add(candidate(prefix + child.getName(), child.getDescription()));
      }
    });
  }
}
