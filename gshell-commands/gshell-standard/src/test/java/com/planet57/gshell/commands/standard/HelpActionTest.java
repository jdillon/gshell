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
package com.planet57.gshell.commands.standard;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.testharness.CommandTestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link HelpAction}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class HelpActionTest
    extends CommandTestSupport
{
  private AliasRegistry aliasRegistry;

  public HelpActionTest() {
    super(HelpAction.class);
  }

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    aliasRegistry = lookup(AliasRegistry.class);
  }

  @Test
  public void testHelpHelp() throws Exception {
    assertTrue(commandRegistry.containsCommand("help"));
    assertFalse(aliasRegistry.containsAlias("foo"));
    Object result = executeCommand("help");
    assertEqualsSuccess(result);
  }

  @Test
  public void testHelpFoo() throws Exception {
    assertFalse(commandRegistry.containsCommand("foo"));
    assertFalse(aliasRegistry.containsAlias("foo"));
    Object result = executeCommand("foo");
    assertEqualsFailure(result);
  }
}
