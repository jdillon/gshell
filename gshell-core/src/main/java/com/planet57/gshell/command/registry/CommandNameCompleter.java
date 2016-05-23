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
package com.planet57.gshell.command.registry;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.planet57.gshell.event.EventListener;
import com.planet57.gshell.event.EventManager;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

/**
 * {@link jline.console.completer.Completer} for command names.
 * Keeps up to date automatically by handling command-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class CommandNameCompleter
    implements Completer
{
    private final EventManager events;

    private final CommandRegistry commands;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public CommandNameCompleter(final EventManager events, final CommandRegistry commands) {
        assert events != null;
        this.events = events;
        assert commands != null;
        this.commands = commands;
    }

    private void init() {
        Collection<String> names = commands.getCommandNames();
        delegate.getStrings().addAll(names);

        // Register for updates to command registrations
        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof CommandRegisteredEvent) {
                    CommandRegisteredEvent target = (CommandRegisteredEvent) event;
                    delegate.getStrings().add(target.getName());
                }
                else if (event instanceof CommandRemovedEvent) {
                    CommandRemovedEvent target = (CommandRemovedEvent) event;
                    delegate.getStrings().remove(target.getName());
                }
            }
        });

        initialized = true;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        if (!initialized) {
            init();
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}