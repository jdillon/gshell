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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.GroupAction;
import com.planet57.gshell.command.registry.CommandRegisteredEvent;
import com.planet57.gshell.command.registry.CommandRegistry;
import com.planet57.gshell.command.registry.CommandRemovedEvent;
import com.planet57.gshell.event.EventAware;
import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandResolver} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class CommandResolverImpl
  extends ComponentSupport
  implements CommandResolver, EventAware
{
  private final Provider<Variables> variables;

  private final Node root;

  @Inject
  public CommandResolverImpl(final Provider<Variables> variables,
                             final CommandRegistry commands)
  {
    this.variables = checkNotNull(variables);
    checkNotNull(commands);

    // Setup the tree
    root = new Node(Node.ROOT, new GroupAction(Node.ROOT));

    // Add any pre-registered commands
    for (CommandAction command : commands.getCommands()) {
      root.add(command.getName(), command);
    }
  }

  @Subscribe
  void on(final CommandRegisteredEvent event) {
    root.add(event.getName(), event.getCommand());
  }

  @Subscribe
  void on(final CommandRemovedEvent event) {
    root.remove(event.getName());
  }

  @Override
  public Node root() {
    return root;
  }

  @Override
  public Node group() {
    Node node;

    Object tmp = variables.get().get(VariableNames.SHELL_GROUP);
    if (tmp instanceof String) {
      node = root.find((String) tmp);
    }
    else if (tmp instanceof Node) {
      node = (Node) tmp;
    }
    else if (tmp == null) {
      node = root;
    }
    else {
      log.warn("Unexpected value for {}: {}", VariableNames.SHELL_GROUP, tmp);
      node = root;
    }

    log.trace("Current group is: {}", node);

    return node;
  }

  @Override
  public List<Node> searchPath() {
    List<Node> path = new ArrayList<>();

    Object tmp = variables.get().get(VariableNames.SHELL_GROUP_PATH);
    if (tmp != null && !(tmp instanceof String)) {
      log.warn("Unexpected value for {}: {}", VariableNames.SHELL_GROUP_PATH, tmp);
      tmp = null;
    }
    if (tmp == null) {
      tmp = String.format("%s%s%s", Node.CURRENT, Node.PATH_SEPARATOR, Node.ROOT);
    }

    Node base = group();
    for (String element : ((String) tmp).split(Node.PATH_SEPARATOR)) {
      Node node = base.find(element);
      if (node == null) {
        log.warn("Invalid search path element: {}", element);
      }
      path.add(node);
    }

    return path;
  }

  @Override
  @Nullable
  public Node resolve(final NodePath path) {
    checkNotNull(path);
    return resolve(path.toString());
  }

  @Override
  @Nullable
  public Node resolve(final String name) {
    checkNotNull(name);
    log.trace("Resolving: {}", name);

    for (Node base : searchPath()) {
      Node node = base.find(name);
      if (node != null) {
        log.trace("Resolved: {} -> {}", name, node);
        return node;
      }

    }

    return null;
  }
}
