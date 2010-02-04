/*
 * Copyright (C) 2009 the original author(s).
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
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandException;
import org.sonatype.gshell.command.registry.CommandRegisteredEvent;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRemovedEvent;
import org.sonatype.gshell.command.registry.NoSuchCommandException;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.vars.Variables;
import static org.sonatype.gshell.vars.VariableNames .*;

import java.util.EventObject;

/**
 * {@link CommandResolver} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class CommandResolverImpl
    implements CommandResolver
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AliasRegistry aliasRegistry;

    private final Provider<Variables> variables;

    private final Node root;

    @Inject
    public CommandResolverImpl(final AliasRegistry aliasRegistry, final Provider<Variables> variables, final EventManager events, final CommandRegistry commandRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
        assert variables != null;
        this.variables = variables;

        // Setup the tree
        root = new Node(ROOT, new GroupAction(ROOT));

        // Add any pre-registered commands
        assert commandRegistry != null;
        for (String name : commandRegistry.getCommandNames()) {
            try {
                root.add(name, commandRegistry.getCommand(name));
            }
            catch (NoSuchCommandException e) {
                // ignore
            }
        }

        // Add a listener to mange the command tree
        assert events != null;
        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                assert event != null;
                if (event instanceof CommandRegisteredEvent) {
                    CommandRegisteredEvent target = (CommandRegisteredEvent)event;
                    root.add(target.getName(), target.getCommand());
                }
                if (event instanceof CommandRemovedEvent) {
                    CommandRemovedEvent target = (CommandRemovedEvent)event;
                    root.remove(target.getName());
                }
            }
        });
    }

    public CommandAction resolveCommand(final String name) throws CommandException {
        assert name != null;

        log.trace("Resolving command: {}", name);

        CommandAction command = resolveAlias(name);
        if (command == null) {
            command = resolveNode(name);
        }

        if (command == null) {
            throw new CommandException("Unable to resolve command: " + name);
        }

        log.trace("Resolved command: {}", command);

        return command;
    }

    private CommandAction resolveAlias(final String name) throws CommandException {
        assert name != null;

        if (aliasRegistry.containsAlias(name)) {
            return new AliasAction(name, aliasRegistry.getAlias(name));
        }

        return null;
    }

    private CommandAction resolveNode(final String name) throws CommandException {
        assert name != null;
        
        Node node = group().find(name);
        if (node != null) {
            return node.getAction();
        }

        return null;
    }

    private Node group() {
        Node node = null;

        Object tmp = variables.get().get(SHELL_GROUP);
        if (tmp instanceof String) {
            node = root.find((String)tmp);
        }
        else if (tmp instanceof Node) {
            node = (Node)tmp;
        }

        if (node == null) {
            node = root;
        }

        log.debug("Current group is: {}", node);

        return node;
    }
}