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
package com.planet57.gshell

import com.planet57.gshell.branding.Branding
import com.planet57.gshell.command.IO
import com.planet57.gshell.shell.Shell
import com.planet57.gshell.testharness.TestBranding
import com.planet57.gshell.util.io.StreamSet
import com.planet57.gshell.variables.Variables
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sonatype.goodies.testsupport.TestSupport

import static org.mockito.Mockito.mock

/**
 * Tests for {@link MainSupport}.
 */
class MainSupportTest
  extends TestSupport
{
  private MockMain underTest

  @Before
  void setUp() {
    underTest = new MockMain()
  }

  @After
  void tearDown() {
    dump()
    underTest = null
  }

  @Test
  void 'option -h'() {
    underTest.boot('-h')

    log(new String(underTest.out.toByteArray()))
    assert underTest.exitCode == 0

    assert outAsString().contains('testsh [options] [arguments]')
  }

  @Test
  void 'option --help'() {
    underTest.boot('--help')

    log(new String(underTest.out.toByteArray()))
    assert underTest.exitCode == 0

    assert outAsString().contains('testsh [options] [arguments]')
  }

  @Test
  void 'option --debug'() {
    underTest.boot('--debug')

    log(new String(underTest.out.toByteArray()))
    assert underTest.exitCode == 0

    assert System.getProperty('shell.logging.console.threshold') == 'DEBUG'
  }

  String outAsString() {
    return new String(underTest.out.toByteArray())
  }

  void dump() {
    println "----8<----\n${outAsString()}\n---->8----"
  }

  private class MockMain
      extends MainSupport
  {
    int exitCode

    ByteArrayInputStream input = new ByteArrayInputStream(new byte[0])

    ByteArrayOutputStream out = new ByteArrayOutputStream()

    @Override
    protected Terminal createTerminal(final Branding branding) {
      return TerminalBuilder.builder().dumb(true).build()
    }

    @Override
    protected StreamSet createStreamSet(final Terminal terminal) {
      return new StreamSet(input, new PrintStream(out, true))
    }

    @Override
    protected Branding createBranding() {
      return new TestBranding(util.resolveFile('target/shell-home'))
    }

    @Override
    protected Shell createShell(final IO io, final Variables variables, final Branding branding) {
      return mock(Shell.class)
    }

    @Override
    protected void exit(int code) {
      this.exitCode = code
    }
  }
}
