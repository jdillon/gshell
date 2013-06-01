/*
 * Copyright (c) 2009-2013 the original author or authors.
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
package org.sonatype.gshell.alias;

import javax.inject.Inject;
import javax.inject.Singleton;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

/**
 * {@link jline.console.completer.Completer} for alias names.
 * Keeps up to date automatically by handling alias-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class AliasNameCompleter
    implements Completer
{
    private final EventManager events;

    private final AliasRegistry aliases;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public AliasNameCompleter(final EventManager events, final AliasRegistry aliases) {
        assert events != null;
        this.events = events;
        assert aliases != null;
        this.aliases = aliases;
    }

    private void init() {
        Map<String,String> aliases = this.aliases.getAliases();
        delegate.getStrings().addAll(aliases.keySet());

        // Register for updates to alias registrations
        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof AliasRegisteredEvent) {
                    AliasRegisteredEvent target = (AliasRegisteredEvent) event;
                    delegate.getStrings().add(target.getName());
                }
                else if (event instanceof AliasRemovedEvent) {
                    AliasRemovedEvent target = (AliasRemovedEvent) event;
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