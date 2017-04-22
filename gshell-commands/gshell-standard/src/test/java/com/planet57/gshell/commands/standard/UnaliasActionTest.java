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

import com.planet57.gshell.testharness.CommandTestSupport;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link UnaliasAction}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class UnaliasActionTest
    extends CommandTestSupport
{
  public UnaliasActionTest() {
    super(UnaliasAction.class);
  }

  @Override
  @Test
  public void testDefault() throws Exception {
    try {
      super.testDefault();
      fail();
    }
    catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testTooManyArguments() throws Exception {
    try {
      executeWithArgs("1 2");
      fail();
    }
    catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testUndefineAlias() throws Exception {
    assertFalse(aliasRegistry.containsAlias("foo"));
    aliasRegistry.registerAlias("foo", "bar");

    Object result = executeWithArgs("foo");
    assertEqualsSuccess(result);
    assertFalse(aliasRegistry.containsAlias("foo"));
  }
}
