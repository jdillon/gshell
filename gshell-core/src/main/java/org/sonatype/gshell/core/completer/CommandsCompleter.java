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

package org.sonatype.gshell.core.completer;

import com.google.inject.Inject;
import jline.console.Completer;
import jline.console.completers.AggregateCompleter;
import jline.console.completers.ArgumentCompleter;
import jline.console.completers.NullCompleter;
import jline.console.completers.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.core.registry.CommandRegisteredEvent;
import org.sonatype.gshell.core.registry.CommandRemovedEvent;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.registry.CommandRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Completer} for commands, including support for command-specific sub-completion.
 *
 * Keeps up to date automatically by handling command-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class CommandsCompleter
    implements Completer
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EventManager eventManager;

    private final CommandRegistry commandRegistry;

    private final Map<String,Completer> completors = new HashMap<String,Completer>();

    private final AggregateCompleter delegate = new AggregateCompleter();

    private boolean initialized;

    @Inject
    public CommandsCompleter(final EventManager eventManager, final CommandRegistry commandRegistry) {
        assert eventManager != null;
        this.eventManager = eventManager;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    private void init() {
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
            throw new RuntimeException(e);
        }

        initialized = true;
    }

    private void addCompleter(final String name) throws Exception {
        assert name != null;

        log.trace("Adding completer for: {}", name);
        
        List<Completer> children = new ArrayList<Completer>();

        // Attach completion for the command name
        children.add(new StringsCompleter(name));

        // Then attach any command specific completers
        CommandAction command = commandRegistry.getCommand(name);

        Completer[] completers = command.getCompleters();
        if (completers == null) {
            children.add(NullCompleter.INSTANCE);
        }
        else {
            for (Completer completer : completers) {
                log.trace("Adding completer: {}", completer);
                children.add(completer != null ? completer : NullCompleter.INSTANCE);
            }
        }

        // Setup the root completer for the command
        Completer root = new ArgumentCompleter(children);

        // Track and attach
        completors.put(name, root);
        delegate.getCompleters().add(root);
    }

    private void removeCompleter(final String name) {
        assert name != null;

        Completer completer = completors.remove(name);
        delegate.getCompleters().remove(completer);
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        if (!initialized) {
            init();
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}