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

package org.apache.gshell.core.impl.completer;

import jline.Completor;
import org.apache.gshell.console.completer.StringsCompleter;
import org.apache.gshell.core.impl.registry.CommandRegisteredEvent;
import org.apache.gshell.core.impl.registry.CommandRemovedEvent;
import org.apache.gshell.event.EventListener;
import org.apache.gshell.event.EventManager;
import org.apache.gshell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

/**
 * {@link Completor} for command names.
 *
 * Keeps up to date automatically by handling command-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Component(role=Completor.class, hint="command-name")
public class CommandNameCompleter
    implements Completor
{
    @Requirement
    private EventManager eventManager;

    @Requirement
    private CommandRegistry commandRegistry;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    public CommandNameCompleter() {}

    public CommandNameCompleter(final EventManager eventManager, final CommandRegistry commandRegistry) {
        assert eventManager != null;
        this.eventManager = eventManager;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    private void init() {
        assert commandRegistry != null;
        Collection<String> names = commandRegistry.getCommandNames();
        delegate.getStrings().addAll(names);

        // Register for updates to command registrations
        eventManager.addListener(new EventListener() {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof CommandRegisteredEvent) {
                    CommandRegisteredEvent targetEvent = (CommandRegisteredEvent)event;
                    delegate.getStrings().add(targetEvent.getName());
                }
                else if (event instanceof CommandRemovedEvent) {
                    CommandRemovedEvent targetEvent = (CommandRemovedEvent)event;
                    delegate.getStrings().remove(targetEvent.getName());
                }
            }
        });

        initialized = true;
    }

    public int complete(final String buffer, final int cursor, final List candidates) {
        if (!initialized) {
            init();
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}