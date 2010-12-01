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

package org.sonatype.gshell.variables;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 * {@link jline.console.completer.Completer} for variable names.
 * Keeps up to date automatically by handling variable-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Singleton
public class VariableNameCompleter
    implements Completer
{
    private final EventManager events;

    private final Provider<Variables> variables;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    @Inject
    public VariableNameCompleter(final EventManager events, final Provider<Variables> variables) {
        assert events != null;
        this.events = events;
        assert variables != null;
        this.variables = variables;
    }

    private void init() {
        // Prime the delegate with any existing variable names
        Iterator<String> iter = variables.get().names();
        while (iter.hasNext()) {
            delegate.getStrings().add(iter.next());
        }

        // Register for updates to variable changes
        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof VariableSetEvent) {
                    VariableSetEvent target = (VariableSetEvent) event;
                    delegate.getStrings().add(target.getName());
                }
                else if (event instanceof VariableUnsetEvent) {
                    VariableUnsetEvent target = (VariableUnsetEvent) event;
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