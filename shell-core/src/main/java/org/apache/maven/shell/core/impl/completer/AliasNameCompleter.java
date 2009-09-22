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

import jline.Completor;
import org.apache.maven.shell.console.completer.StringsCompleter;
import org.apache.maven.shell.core.impl.registry.AliasRegisteredEvent;
import org.apache.maven.shell.core.impl.registry.AliasRemovedEvent;
import org.apache.maven.shell.event.EventListener;
import org.apache.maven.shell.event.EventManager;
import org.apache.maven.shell.registry.AliasRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

/**
 * {@link Completor} for alias names.
 *
 * Keeps up to date automatically by handling alias-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Completor.class, hint="alias-name")
public class AliasNameCompleter
    implements Completor
{
    @Requirement
    private EventManager eventManager;

    @Requirement
    private AliasRegistry aliasRegistry;

    private final StringsCompleter delegate = new StringsCompleter();

    private boolean initialized;

    public AliasNameCompleter() {}
    
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
        eventManager.addListener(new EventListener() {
            public void onEvent(final EventObject event) throws Exception {
                if (event instanceof AliasRegisteredEvent) {
                    AliasRegisteredEvent targetEvent = (AliasRegisteredEvent)event;
                    delegate.getStrings().add(targetEvent.getName());
                }
                else if (event instanceof AliasRemovedEvent) {
                    AliasRemovedEvent targetEvent = (AliasRemovedEvent)event;
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