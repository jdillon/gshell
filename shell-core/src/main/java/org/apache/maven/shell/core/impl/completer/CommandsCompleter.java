/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.core.impl.completer;

import jline.ArgumentCompletor;
import jline.Completor;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.console.completer.TerminalCompleter;
import org.apache.maven.shell.console.completer.StringsCompleter;
import org.apache.maven.shell.core.impl.registry.CommandRegisteredEvent;
import org.apache.maven.shell.core.impl.registry.CommandRemovedEvent;
import org.apache.maven.shell.event.EventListener;
import org.apache.maven.shell.event.EventManager;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Completor} for commands, including support for command-specific sub-completion.
 *
 * Keeps up to date automatically by handling command-related events.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Completor.class, hint="commands")
public class CommandsCompleter
    implements Completor, Initializable
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private EventManager eventManager;

    @Requirement
    private CommandRegistry commandRegistry;

    private final Map<String,Completor> completors = new HashMap<String,Completor>();

    private final AggregateCompleter delegate = new AggregateCompleter();

    public void initialize() throws InitializationException {
        try {
            // Populate the initial list of completers from the currently registered commands
            Collection<String> names = commandRegistry.getCommandNames();
            for (String name : names) {
                addCompleter(name);
            }

            // Register for updates to command registrations
            eventManager.addListener(new EventListener() {
                public void onEvent(final EventObject event) throws Exception {
                    if (event instanceof CommandRegisteredEvent) {
                        CommandRegisteredEvent targetEvent = (CommandRegisteredEvent)event;
                        addCompleter(targetEvent.getName());
                    }
                    else if (event instanceof CommandRemovedEvent) {
                        CommandRemovedEvent targetEvent = (CommandRemovedEvent)event;
                        removeCompleter(targetEvent.getName());
                    }
                }
            });
        }
        catch (Exception e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    private void addCompleter(final String name) throws Exception {
        assert name != null;

        log.debug("Adding completer for: {}", name);
        
        List<Completor> children = new ArrayList<Completor>();

        // Attach completion for the command name
        children.add(new StringsCompleter(new String[] { name }));

        // Then attach any command specific completers
        Command command = commandRegistry.getCommand(name);

        Completor[] completers = command.getCompleters();
        if (completers != null) {
            for (Completor completer : completers) {
                log.debug("Adding completer: {}", completer);
                children.add(completer != null ? completer : TerminalCompleter.INSTANCE);
            }
        }
        else {
            children.add(TerminalCompleter.INSTANCE);
        }

        // setUp the root completer for the command
        Completor root = new ArgumentCompletor(children);

        // Track and attach
        completors.put(name, root);
        delegate.getCompleters().add(root);
    }

    private void removeCompleter(final String name) {
        assert name != null;

        Completor completer = completors.remove(name);
        delegate.getCompleters().remove(completer);
    }

    public int complete(final String buffer, final int cursor, final List candidates) {
        return delegate.complete(buffer, cursor, candidates);
    }
}