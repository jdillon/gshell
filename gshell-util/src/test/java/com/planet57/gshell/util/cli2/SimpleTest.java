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
package com.planet57.gshell.util.cli2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Some simple tests to validate basic functionality.
 */
public class SimpleTest
    extends CliProcessorTestSupport
{
  private static class Simple
  {
    @Option(name = "h", longName = "help")
    boolean help;

    @Option(name = "v", longName = "verbose")
    Boolean verbose;

    @Argument
    String arg1;
  }

  private Simple bean;

  @Override
  protected Object createBean() {
    bean = new Simple();
    return bean;
  }

  @Test
  public void testName() throws Exception {
    underTest.process("-v");

    assertFalse(bean.help);
    assertTrue(bean.verbose);
  }

  @Test
  public void testName2() throws Exception {
    underTest.process("-h");

    assertTrue(bean.help);
    assertNull(bean.verbose);
  }

  @Test
  public void testLongName() throws Exception {
    underTest.process("--help");

    assertTrue(bean.help);
    assertNull(bean.verbose);
  }

  @Test
  public void testInvalidOption() throws Exception {
    try {
      underTest.process("-f");
      fail();
    }
    catch (Exception e) {
      // ignore
    }

    assertFalse(bean.help);
    assertNull(bean.verbose);
  }

  @Test
  public void testArg() throws Exception {
    underTest.process("foo");

    assertEquals(bean.arg1, "foo");
    assertFalse(bean.help);
    assertNull(bean.verbose);
  }

  @Test
  public void testTooManyArgs() throws Exception {
    try {
      underTest.process("foo", "bar", "baz");
      fail();
    }
    catch (Exception e) {
      // ignore
    }
  }
}
