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

import javax.inject.Singleton;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.GroupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A node for building a {@link CommandAction} tree.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class Node
{
  public static final String ROOT = "/";

  public static final String PARENT = "..";

  public static final String CURRENT = ".";

  public static final String SEPARATOR = "/";

  public static final String PATH_SEPARATOR = ":";

  private static final Logger log = LoggerFactory.getLogger(Node.class);

  private final String name;

  private final CommandAction action;

  private final Node parent;

  private final Collection<Node> children = new LinkedHashSet<Node>();

  public Node(final String name, final CommandAction action, final Node parent) {
    assert name != null;
    this.name = name;
    assert action != null;
    this.action = action;
    // parent can be null
    this.parent = parent;
  }

  public Node(final String name, final CommandAction action) {
    this(name, action, null);
  }

  public String getName() {
    return name;
  }

  // FIXME: This should return a NodePath
  public String getPath() {
    // FIXME: This is sloppy/icky
    if (isRoot()) {
      return ROOT;
    }
    if (getParent().isRoot()) {
      return String.format("%s%s", ROOT, getName());
    }
    return String.format("%s%s%s", getParent().getPath(), SEPARATOR, getName());
  }

  public CommandAction getAction() {
    return action;
  }

  public Node getParent() {
    return parent;
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  public boolean isGroup() {
    return action instanceof GroupAction;
  }

  public Node root() {
    Node node = this;
    while (!node.isRoot()) {
      node = node.parent;
    }
    return node;
  }

  public Node get(final String name) {
    assert name != null;

    if (name.equals(ROOT)) {
      return root();
    }
    if (name.equals(PARENT)) {
      if (parent != null) {
        return parent;
      }
      // resolve '..' as '.' when there is no parent
      return this;
    }
    if (name.equals(CURRENT)) {
      return this;
    }

    for (Node child : children) {
      if (child.name.equals(name)) {
        return child;
      }
    }

    return null;
  }

  public Collection<Node> children() {
    return children;
  }

  public Collection<Node> children(final String name) {
    if (name == null) {
      return children;
    }

    Collection<Node> nodes = new LinkedHashSet<Node>();

    for (Node child : children) {
      if (child.name.startsWith(name)) {
        nodes.add(child);
      }
    }

    return nodes;
  }

  public Node find(final String name) {
    assert name != null;

    NodePath path = new NodePath(name);
    Node node = this;
    String[] elements = path.split();

    for (String element : elements) {
      node = node.get(element);
      if (node == null) {
        break;
      }
    }

    // If we are looking for a group node, but given name ends with / then return null
    if (node != null && !node.isRoot() && !node.isGroup() && name.endsWith(SEPARATOR)) {
      return null;
    }

    return node;
  }

  public Node find(final NodePath path) {
    assert path != null;
    return find(path.toString());
  }

  public void add(final String name, final CommandAction command) {
    assert name != null;
    assert command != null;

    NodePath path = new NodePath(name);
    String[] elements = path.split();

    Node current = this;
    for (int i = 0; i < elements.length; i++) {
      Node node = current.get(elements[i]);

      if (i + 1 == elements.length) {
        // this is the last element, node should be null
        if (node != null) {
          throw new RuntimeException("Invalid path; found existing node: " + elements[i] + " at the end of: " + name);
        }
        node = new Node(elements[i], command, current);
        current.children.add(node);
        log.trace("Added command node: {} in parent: {}", node.name, node.parent.name);
      }
      else {
        // in the middle of the path, add a new group if one does not exist already
        if (node == null) {
          // FIXME: This is sloppy/icky, perhaps to simplify have group node return "/" suffix?
          String group;
          if (current.isRoot()) {
            group = String.format("%s%s", ROOT, elements[i]);
          }
          else {
            group = String.format("%s%s%s", current.getPath(), SEPARATOR, elements[i]);
          }
          node = new Node(elements[i], new GroupAction(group), current);
          current.children.add(node);
          log.trace("Added group node: {}", group);
        }
        else if (!node.isGroup()) {
          throw new RuntimeException("Invalid path; found non-group action: " + elements[i] + " in middle of: " + name);
        }

      }

      current = node;
    }
  }

  public void remove(final String name) {
    assert name != null;

    Node node = find(name);
    if (node != null) {
      node.children.remove(node);
    }
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return "Node{" +
        "name='" + name + '\'' +
        ", path=" + getPath() +
        ", root=" + isRoot() +
        ", leaf=" + isLeaf() +
        ", action=" + action +
        '}';
  }
}