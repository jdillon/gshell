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
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.GroupAction;
import org.sonatype.gshell.command.registry.CommandRegisteredEvent;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRemovedEvent;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.vars.Variables;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import static org.sonatype.gshell.command.resolver.Node.CURRENT;
import static org.sonatype.gshell.command.resolver.Node.PATH_SEPARATOR;
import static org.sonatype.gshell.command.resolver.Node.ROOT;
import static org.sonatype.gshell.vars.VariableNames.SHELL_GROUP;
import static org.sonatype.gshell.vars.VariableNames.SHELL_GROUP_PATH;

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

    private final Provider<Variables> variables;

    private final Node root;

    @Inject
    public CommandResolverImpl(final Provider<Variables> variables, final EventManager events, final CommandRegistry commands) {
        assert variables != null;
        this.variables = variables;

        // Setup the tree
        root = new Node(ROOT, new GroupAction(ROOT));

        // Add any pre-registered commands
        assert commands != null;
        for (CommandAction command : commands.getCommands()) {
            root.add(command.getName(), command);
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

    public Node resolve(final String name) {
        assert name != null;

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

    public Node group() {
        Node node;
        
        Object tmp = variables.get().get(SHELL_GROUP);
        if (tmp instanceof String) {
            node = root.find((String)tmp);
        }
        else if (tmp instanceof Node) {
            node = (Node)tmp;
        }
        else if (tmp == null) {
            node = root;
        }
        else {
            log.warn("Unexpected value for {}: {}", SHELL_GROUP, tmp);
            node = root;
        }

        log.trace("Current group is: {}", node);

        return node;
    }

    public List<Node> searchPath() {
        List<Node> path = new ArrayList<Node>();

        Object tmp = variables.get().get(SHELL_GROUP_PATH);
        if (tmp != null && !(tmp instanceof String)) {
            log.warn("Unexpected value for {}: {}", SHELL_GROUP_PATH, tmp);
            tmp = null;
        }
        if (tmp == null) {
            tmp = String.format("%s%s%s", CURRENT, PATH_SEPARATOR, ROOT);
        }

        Node base = group();
        for (String element : ((String)tmp).split(PATH_SEPARATOR)) {
            Node node = base.find(element);
            if (node == null) {
                log.warn("Invalid search path element: {}", element);
            }
            path.add(node);
        }

        return path;
    }
}