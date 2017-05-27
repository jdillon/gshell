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
package com.planet57.gshell.util.cli2

import org.junit.Test

/**
 * Tests options.
 */
class OptionsTest
    extends CliProcessorTestSupport
{
  private static class Simple
  {
    @Option(name = 'h', longName = 'help')
    boolean help

    @Option(name = 'v', optionalArg = true)
    Boolean verbose

    @Option(name = 's')
    String string

    @Option(name = 'S', args = 2)
    List<String> strings

    private boolean debug

    @Option(name = 'd', longName = 'debug', optionalArg = true)
    private void setDebug(final boolean flag) {
      debug = flag
    }
  }

  private Simple bean

  @Override
  protected Object createBean() {
    return bean = new Simple()
  }

  @Test
  void testHelp() {
    underTest.process('-h')
    assert bean.help
  }

  @Test
  void testHelp2() {
    underTest.process('--help')
    assert bean.help
  }

  @Test
  void testVerbose() {
    underTest.process('-v')
    assert bean.verbose
  }

  @Test
  void testVerbose2() {
    underTest.process('-v', 'false')
    assert !bean.verbose
  }

  @Test
  void testDebug() {
    underTest.process('--debug')
    assert bean.debug
  }

  @Test
  void testDebugFalse() {
    underTest.process('--debug=false')
    assert !bean.debug
  }

  @Test
  void testDebugTrue() {
    underTest.process('--debug=true')
    assert bean.debug
  }

  @Test
  void testString() {
    try {
      underTest.process('-s')
      assert false
    }
    catch (Exception e) {
      // expected
    }

    underTest.process('-s', 'foo')
    assert bean.string == 'foo'
  }

  @Test
  void testStrings() {
    try {
      underTest.process('-S')
      assert false
    }
    catch (Exception e) {
      // expected
    }

    underTest.process('-S', 'foo', 'bar')
    assert bean.strings[0] == 'foo'
    assert bean.strings[1] == 'bar'
  }

  @Test
  void testStrings2() {
    try {
      underTest.process('-S', 'foo', 'bar', 'baz')
      assert false
    }
    catch (Exception e) {
      // expected
    }
  }
}
