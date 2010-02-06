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

package org.sonatype.gshell.command.registry;

import com.google.inject.Inject;
import jline.console.Completer;
import jline.console.completers.StringsCompleter;
import org.sonatype.gshell.command.registry.CommandRegisteredEvent;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.command.registry.CommandRemovedEvent;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

/**
 * {@link Completer} for command names.
 * <p/>
 * Keeps up to date automatically by handling command-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandNameCompleter
    implements Completer
{
    private final EventManager events;

    private final CommandRegistry commandRegistry;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public CommandNameCompleter(final EventManager events, final CommandRegistry commandRegistry) {
        assert events != null;
        this.events = events;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    private void init() {
        assert commandRegistry != null;
        Collection<String> names = commandRegistry.getCommandNames();
        delegate.getStrings().addAll(names);

        // Register for updates to command registrations
        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof CommandRegisteredEvent) {
                    CommandRegisteredEvent targetEvent = (CommandRegisteredEvent) event;
                    delegate.getStrings().add(targetEvent.getName());
                }
                else if (event instanceof CommandRemovedEvent) {
                    CommandRemovedEvent targetEvent = (CommandRemovedEvent) event;
                    delegate.getStrings().remove(targetEvent.getName());
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