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

package org.apache.maven.shell.core.impl;

import org.apache.maven.shell.event.EventListener;
import org.apache.maven.shell.event.EventManager;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The default {@link EventManager} components.
 *
 * @version $Rev$ $Date$
 */
@Component(role=EventManager.class)
public class EventManagerImpl
    implements EventManager
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Set<EventListener> listeners = new LinkedHashSet<EventListener>();

    //
    // EventManager
    //

    public void addListener(final EventListener listener) {
        assert listener != null;

        log.trace("Adding listener: {}", listener);

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(final EventListener listener) {
        assert listener != null;
        
        log.trace("Removing listener: {}", listener);

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void publish(final EventObject event) {
        assert event != null;

        log.trace("Publishing event: {}", event);

        EventListener[] targets;
        
        synchronized (listeners) {
            targets = listeners.toArray(new EventListener[listeners.size()]);
        }

        for (EventListener listener : targets) {
            log.trace("Firing event ({}) to listener: {}", event, listener);

            try {
                listener.onEvent(event);
            }
            catch (Exception e) {
                log.error("Listener handler raised an exception", e);
            }
        }
    }
}