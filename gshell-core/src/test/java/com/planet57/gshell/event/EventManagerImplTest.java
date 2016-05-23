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
package com.planet57.gshell.event;

import java.util.EventObject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link EventManagerImpl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EventManagerImplTest
{
    private EventManager manager;

    private MockEventListener listener;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule() {
            @Override
            protected void configure() {
                bind(EventManager.class).to(EventManagerImpl.class);
            }
        });
        manager = injector.getInstance(EventManager.class);
        listener = new MockEventListener();
    }

    @After
    public void tearDown() {
        manager = null;
        listener = null;
    }

    @Test
    public void testAddListener() throws Exception {
        try {
            manager.addListener(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        manager.addListener(listener);
    }

    @Test
    public void testRemoveListener() throws Exception {
        try {
            manager.removeListener(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        // Can remove a listener which was never added, just ignored
        manager.removeListener(listener);
    }

    @Test
    public void testPublish() throws Exception {
        try {
            manager.publish(null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        EventObject event = new EventObject("test");
        manager.publish(event);
    }
    
    @Test
    public void testPublishWithListener() throws Exception {
        manager.addListener(listener);

        EventObject event = new EventObject("test");

        manager.publish(event);

        assertEquals(event, listener.event);
    }

    @Test
    public void testPublishWithManyListener() throws Exception {
        manager.addListener(listener);

        MockEventListener anotherListener = new MockEventListener();
        manager.addListener(anotherListener);

        EventObject event = new EventObject("test");

        manager.publish(event);

        assertEquals(event, listener.event);
        assertEquals(event, anotherListener.event);
    }

    //
    // MockEventListener
    //
    
    private static class MockEventListener
        implements EventListener
    {
        public EventObject event;

        public void onEvent(final EventObject event) throws Exception {
            this.event = event;
        }
    }
}