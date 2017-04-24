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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.event.EventManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import javax.annotation.Nonnull;

import static org.junit.Assert.fail;

/**
 * Tests for the {@link CommandRegistryImpl}.
 */
public class CommandRegistryImplTest
  extends TestSupport
{
  private CommandRegistryImpl underTest;

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, (Module) binder -> {
      binder.bind(EventManager.class).to(EventManagerImpl.class);
      binder.bind(CommandRegistry.class).to(CommandRegistryImpl.class);
    });
    underTest = injector.getInstance(CommandRegistryImpl.class);
  }

  @After
  public void tearDown() {
    underTest = null;
  }

  @Test
  public void testRegisterCommandInvalid() throws Exception {
    try {
      underTest.registerCommand(null, null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      underTest.registerCommand("foo", null);
      fail();
    }
    catch (NullPointerException e) {
      // ignore
    }

    try {
      underTest.registerCommand(null, new CommandActionSupport()
      {
        public Object execute(@Nonnull CommandContext context) throws Exception {
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
