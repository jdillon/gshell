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
import java.util.LinkedHashSet;
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

    Collection<Node> matches = new LinkedHashSet<>();

    // TODO: handle ./, ../ and / and general location information
//    String word = line.word();
//    log.trace("Completing; {}; word: {}, line: {}, words: {}", line, word, line.line(), line.words());

    resolver.searchPath().forEach(parent -> matches.addAll(parent.children()));

    if (log.isTraceEnabled()) {
      log.trace("Matched:");
      for (Node node : matches) {
        log.trace("  {}", node);
      }
    }

    // append all matching nodes
    matches.forEach(node -> {
      if (node.isRoot()) {
        appendChildren(candidates, node, "");
      }
      else if (node.isGroup()) {
        String name = node.getName() + SEPARATOR;
        candidates.add(candidate(name, node.getDescription()));
        appendChildren(candidates, node, "");
      }
      else {
        candidates.add(candidate(node.getName(), node.getDescription()));
      }
    });

    if (log.isTraceEnabled()) {
      log.trace("Candidates:");
      candidates.forEach(candidate -> {
        log.trace("  {}", candidate);
      });
    }
  }

  private static void appendChildren(final Collection<Candidate> candidates, final Node parent, final String prefix) {
    assert parent.isGroup();

    String path = prefix + (parent.isRoot() ? "" : parent.getName()) + SEPARATOR;

    parent.children().forEach(child -> {
      if (child.isGroup()) {
        candidates.add(candidate(path + child.getName() + SEPARATOR, child.getDescription()));
        appendChildren(candidates, child, path);
      }
      else {
        candidates.add(candidate(path + child.getName(), child.getDescription()));
      }
    });
  }
}
