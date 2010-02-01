/*
 * Copyright (C) 2010 the original author or authors.
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
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.help.HelpPageManager;
import org.sonatype.gshell.help.MetaHelpPage;
import org.sonatype.gshell.help.MetaHelpPageAddedEvent;
import org.sonatype.gshell.registry.AliasRegisteredEvent;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.AliasRemovedEvent;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

/**
 * {@link jline.console.Completer} for meta help page names.
 * <p/>
 * Keeps up to date automatically by handling meta-page-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class MetaHelpPageNameCompleter
    implements Completer
{
    private final EventManager eventManager;

    private final HelpPageManager helpPages;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public MetaHelpPageNameCompleter(final EventManager eventManager, final HelpPageManager helpPages) {
        assert eventManager != null;
        this.eventManager = eventManager;
        assert helpPages != null;
        this.helpPages = helpPages;
    }

    private void init() {
        for (MetaHelpPage page : helpPages.getMetaPages()) {
            delegate.getStrings().add(page.getName());
        }

        // Register for updates to alias registrations
        eventManager.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof MetaHelpPageAddedEvent) {
                    MetaHelpPageAddedEvent targetEvent = (MetaHelpPageAddedEvent) event;
                    delegate.getStrings().add(targetEvent.getDescriptor().getName());
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