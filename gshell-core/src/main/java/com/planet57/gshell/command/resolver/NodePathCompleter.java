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

import com.planet57.gshell.util.ComponentSupport;
import com.planet57.gshell.util.jline.StringsCompleter2;
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
import static com.planet57.gshell.command.resolver.Node.CURRENT;
import static com.planet57.gshell.command.resolver.Node.PARENT;
import static com.planet57.gshell.command.resolver.Node.ROOT;
import static com.planet57.gshell.command.resolver.Node.SEPARATOR;

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

    Collection<Node> matches = new LinkedHashSet<>();
    String prefix = "";
    String word = line.word();
    log.trace("Completing; {}; word: {}, line: {}, words: {}", line, word, line.line(), line.words());

    if (word.startsWith(ROOT) || word.startsWith(CURRENT)) {
      NodePath path = new NodePath(word);
      prefix = getPrefix(word);
      Node node = resolver.resolve(path);
      log.trace("Resolving from ROOT or CURRENT; prefix: {} node: {}", prefix, node);

      if (node != null) {
        // direct match found
        if (node.isGroup() && word.endsWith(SEPARATOR)) {
          log.trace("Direct group match found");
          // if the node is a group and the buffer ends with /, then match children
          prefix = word;
          matches.addAll(node.children());
        }
        else if (node.isGroup() && word.endsWith(PARENT)) {
          log.trace("Direct parent-group match found");
          candidates.add(StringsCompleter2.candidate(word + SEPARATOR));
        }
        else if (node.isGroup() && word.endsWith(CURRENT)) {
          log.trace("Direct current match found");
          candidates.add(StringsCompleter2.candidate(word + SEPARATOR));
          candidates.add(StringsCompleter2.candidate(word + CURRENT + SEPARATOR));
        }
        else {
          log.trace("Direct match found");
          matches.add(node);
        }
      }
      else {
        path = path.parent();
        log.trace("Searching for match in path: {}", path);
        node = resolver.resolve(path);

        if (node != null && node.isGroup()) {
          String suffix = getSuffix(word);
          for (Node child : node.children()) {
            if (child.getName().startsWith(suffix)) {
              log.trace("Matched: {}", child);
              matches.add(child);
            }
          }
        }
      }
    }
    else {
      // need to look for matches in the group search path
      log.trace("Consult search path");

      for (Node parent : resolver.searchPath()) {
        Node node = parent.find(word);

        if (node != null) {
          // direct match found
          if (node.isGroup() && word.endsWith(SEPARATOR)) {
            // if the node is a group and the buffer ends with /, then match children
            prefix = word;
            matches.addAll(node.children());
          }
          else {
            // else match the single node, if its a group, next match will append /
            matches.add(node);
          }
        }
        else {
          // no direct match, find the parent node and match its children by name
          NodePath path = new NodePath(word);
          path = path.parent();
          if (path != null) {
            node = parent.find(path);
          }
          else {
            node = parent;
          }

          if (node != null && node.isGroup()) {
            // if the buffer contains a /, then set the prefix
            if (word.contains(SEPARATOR)) {
              prefix = getPrefix(word);
            }

            // look for all nodes matching the suffix
            String suffix = getSuffix(word);
            for (Node child : node.children()) {
              if (child.getName().startsWith(suffix)) {
                matches.add(child);
              }
            }
          }
        }
      }
    }

    buildCandidates(candidates, matches, prefix);
  }

  protected void buildCandidates(final List<Candidate> candidates, final Collection<Node> matches, final String prefix) {
    assert candidates != null;
    assert matches != null;
    assert prefix != null;

    for (Node node : matches) {
      String path = prefix + node.getName() + (node.isLeaf() ? "" : (node.getName().equals(ROOT) ? "" : SEPARATOR));
      log.trace("Including candidate: {}", path);
      candidates.add(StringsCompleter2.candidate(path));
    }
  }

  /**
   * Returns the portion of buffer from the beginning up until the last "/".
   */
  private String getPrefix(final String buffer) {
    assert buffer != null;
    int i = buffer.lastIndexOf(SEPARATOR);
    if (i != -1) {
      return buffer.substring(0, buffer.lastIndexOf(SEPARATOR) + 1);
    }
    return buffer;
  }

  /**
   * Returns the portion of the buffer from the last "/" until end.
   */
  private String getSuffix(final String buffer) {
    assert buffer != null;
    int i = buffer.lastIndexOf(SEPARATOR);
    if (i != -1) {
      return buffer.substring(i + 1, buffer.length());
    }
    return buffer;
  }
}
