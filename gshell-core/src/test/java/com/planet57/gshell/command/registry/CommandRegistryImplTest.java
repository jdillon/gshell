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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.event.EventManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Tests for the {@link CommandRegistryImpl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CommandRegistryImplTest
{
  private CommandRegistry registry;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule()
    {
      @Override
      protected void configure() {
        bind(EventManager.class).to(EventManagerImpl.class);
        bind(CommandRegistry.class).to(CommandRegistryImpl.class);
      }
    });
    registry = injector.getInstance(CommandRegistry.class);
  }

  @After
  public void tearDown() {
    registry = null;
  }

  @Test
  public void testRegisterCommandInvalid() throws Exception {
    try {
      registry.registerCommand(null, null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      registry.registerCommand("foo", null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      registry.registerCommand(null, new CommandActionSupport()
      {
        public Object execute(CommandContext context) throws Exception {
          // ignore
          return null;
        }
      });
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }
  }
}
