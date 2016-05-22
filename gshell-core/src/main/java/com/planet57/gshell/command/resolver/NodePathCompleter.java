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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import jline.console.completer.Completer;

import static com.planet57.gshell.command.resolver.Node.CURRENT;
import static com.planet57.gshell.command.resolver.Node.PARENT;
import static com.planet57.gshell.command.resolver.Node.ROOT;
import static com.planet57.gshell.command.resolver.Node.SEPARATOR;

/**
 * {@link jline.console.completer.Completer} for node path names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class NodePathCompleter
    implements Completer
{
    private final CommandResolver resolver;

    @Inject
    public NodePathCompleter(final CommandResolver resolver) {
        assert resolver != null;
        this.resolver = resolver;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer can be null
        assert candidates != null;

        Collection<Node> matches = new LinkedHashSet<Node>();
        String prefix = "";

        if (buffer == null || buffer.length() == 0) {
            // If there is no context in the buffer, then match every node in the search path
            for (Node parent : resolver.searchPath()) {
                matches.addAll(parent.children());
            }

            if (!resolver.group().isRoot()) {
                candidates.add(PARENT);
            }
        }
        else if (buffer.startsWith(ROOT) || buffer.startsWith(CURRENT)) {
            NodePath path = new NodePath(buffer);

            // If the path starts with / or . (..) then no need to use the group search path
            Node node = resolver.resolve(path);
            prefix = getPrefix(buffer);

            if (node != null) {
                // direct match found
                if (node.isGroup() && buffer.endsWith(SEPARATOR)) {
                    // if the node is a group and the buffer ends with /, then match children
                    prefix = buffer;
                    matches.addAll(node.children());
                }
                else if (node.isGroup() && buffer.endsWith(PARENT)) {
                    candidates.add(buffer + SEPARATOR);
                }
                else if (node.isGroup() && buffer.endsWith(CURRENT)) {
                    candidates.add(buffer + SEPARATOR);
                    candidates.add(buffer + CURRENT + SEPARATOR);
                }
                else {
                    // else match the single node, if its a group, next match will append /
                    matches.add(node);
                }
            }
            else {
                path = path.parent();
                node = resolver.resolve(path);

                if (node != null && node.isGroup()) {
                    String suffix = getSuffix(buffer);
                    for (Node child : node.children()) {
                        if (child.getName().startsWith(suffix)) {
                            matches.add(child);
                        }
                    }
                }
            }
        }
        else {
            // need to look for matches in the group search path
            for (Node parent : resolver.searchPath()) {
                Node node = parent.find(buffer);

                if (node != null) {
                    // direct match found
                    if (node.isGroup() && buffer.endsWith(SEPARATOR)) {
                        // if the node is a group and the buffer ends with /, then match children
                        prefix = buffer;
                        matches.addAll(node.children());
                    }
                    else {
                        // else match the single node, if its a group, next match will append /
                        matches.add(node);
                    }
                }
                else {
                    // no direct match, find the parent node and match its children by name
                    NodePath path = new NodePath(buffer);
                    path = path.parent();
                    if (path != null) {
                        node = parent.find(path);
                    }
                    else {
                        node = parent;
                    }

                    if (node != null && node.isGroup()) {
                        // if the buffer contains a /, then set the prefix
                        if (buffer.contains(SEPARATOR)) {
                            prefix = getPrefix(buffer);
                        }

                        // look for all nodes matching the suffix
                        String suffix = getSuffix(buffer);
                        for (Node child : node.children()) {
                            if (child.getName().startsWith(suffix)) {
                                matches.add(child);
                            }
                        }
                    }
                }
            }
        }

        return buildCandidates(candidates, matches, prefix);
    }

    protected int buildCandidates(final List<CharSequence> candidates, final Collection<Node> matches, final String prefix) {
        assert candidates != null;
        assert matches != null;
        assert prefix != null;

        if (matches.size() == 1) {
            Node node = matches.iterator().next();
            candidates.add(prefix + node.getName() + (node.isLeaf() ? " " :
                (node.getName().equals(ROOT) ? "" : SEPARATOR)));
        }
        else {
            for (Node node : matches) {
                candidates.add(prefix + node.getName() + (node.isLeaf() ? "" :
                    (node.getName().equals(ROOT) ? "" : SEPARATOR)));
            }
        }

        return candidates.isEmpty() ? -1 : 0;
    }

    private String getPrefix(final String buffer) {
        assert buffer != null;
        int i = buffer.lastIndexOf(SEPARATOR);
        if (i != -1) {
            return buffer.substring(0, buffer.lastIndexOf(SEPARATOR) + 1);
        }
        return buffer;
    }

    private String getSuffix(final String buffer) {
        assert buffer != null;
        int i = buffer.lastIndexOf(SEPARATOR);
        if (i != -1) {
            return buffer.substring(i+1, buffer.length());
        }
        return buffer;
    }
}