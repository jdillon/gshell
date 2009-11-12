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

package org.sonatype.gshell.console.completer;

import com.google.inject.Inject;
import jline.console.Completer;
import jline.console.completers.StringsCompleter;
import org.sonatype.gshell.registry.AliasRegisteredEvent;
import org.sonatype.gshell.registry.AliasRemovedEvent;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.registry.AliasRegistry;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

/**
 * {@link Completer} for alias names.
 * <p/>
 * Keeps up to date automatically by handling alias-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class AliasNameCompleter
    implements Completer
{
    private final EventManager eventManager;

    private final AliasRegistry aliasRegistry;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public AliasNameCompleter(final EventManager eventManager, final AliasRegistry aliasRegistry) {
        assert eventManager != null;
        this.eventManager = eventManager;
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
    }

    private void init() {
        assert aliasRegistry != null;
        Collection<String> names = aliasRegistry.getAliasNames();
        delegate.getStrings().addAll(names);

        // Register for updates to alias registrations
        eventManager.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof AliasRegisteredEvent) {
                    AliasRegisteredEvent targetEvent = (AliasRegisteredEvent) event;
                    delegate.getStrings().add(targetEvent.getName());
                }
                else if (event instanceof AliasRemovedEvent) {
                    AliasRemovedEvent targetEvent = (AliasRemovedEvent) event;
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