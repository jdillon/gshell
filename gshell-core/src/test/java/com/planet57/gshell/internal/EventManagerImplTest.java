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
package com.planet57.gshell.internal;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.event.EventManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link EventManagerImpl}.
 */
public class EventManagerImplTest
  extends TestSupport
{
  private EventManagerImpl underTest;

  private MockEventListener listener;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, (Module) binder -> {
      binder.bind(EventManager.class).to(EventManagerImpl.class);
    });
    underTest = injector.getInstance(EventManagerImpl.class);
    listener = new MockEventListener();
  }

  @After
  public void tearDown() {
    underTest = null;
    listener = null;
  }

  @Test
  public void testAddListener() throws Exception {
    try {
      underTest.register(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    underTest.register(listener);
  }

  @Test
  public void testRemoveListener() throws Exception {
    try {
      underTest.unregister(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      underTest.unregister(listener);
    }
    catch (IllegalArgumentException e) {
      // ignore
    }
  }

  @Test
  public void testPublish() throws Exception {
    try {
      underTest.publish(null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    Object event = new Object();
    underTest.publish(event);
  }

  @Test
  public void testPublishWithListener() throws Exception {
    underTest.register(listener);

    Object event = new Object();

    underTest.publish(event);

    assertEquals(event, listener.event);
  }

  @Test
  public void testPublishWithManyListener() throws Exception {
    underTest.register(listener);

    MockEventListener anotherListener = new MockEventListener();
    underTest.register(anotherListener);

    Object event = new Object();

    underTest.publish(event);

    assertEquals(event, listener.event);
    assertEquals(event, anotherListener.event);
  }

  //
  // MockEventListener
  //

  private static class MockEventListener
  {
    Object event;

    @Subscribe
    public void on(final Object event) throws Exception {
      this.event = event;
    }
  }
}
