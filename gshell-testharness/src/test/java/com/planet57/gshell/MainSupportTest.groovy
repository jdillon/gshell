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

import com.google.common.base.CharMatcher
import com.planet57.gossip.Level
import com.planet57.gshell.branding.Branding
import com.planet57.gshell.util.io.IO
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

    assert underTest.exitCode == 0
    assert outAsString().contains('testsh [options] [arguments]')
  }

  @Test
  void 'option --help'() {
    underTest.boot('--help')

    assert underTest.exitCode == 0
    assert outAsString().contains('testsh [options] [arguments]')
  }

  @Test
  void 'option --debug'() {
    underTest.boot('--debug')

    assert underTest.exitCode == 0
    assert underTest.loggingLevel == Level.DEBUG

    // TODO: verify logging was actually enabled; presently due to use of same logging system in embedded mode this is not possible
  }

  String outAsString() {
    return new String(underTest.out.toByteArray())
  }

  void dump() {
    def out = CharMatcher.whitespace().trimTrailingFrom(outAsString())
    println "----8<----\n${out}\n---->8----"
  }

  private class MockMain
      extends MainSupport
  {
    Integer exitCode

    Level loggingLevel

    ByteArrayInputStream input = new ByteArrayInputStream(new byte[0])

    ByteArrayOutputStream out = new ByteArrayOutputStream()

    @Override
    protected Branding createBranding() {
      return new TestBranding(util.resolveFile('target/shell-home'))
    }

    @Override
    protected void setupLogging(final Level level) {
      loggingLevel = level;
      super.setupLogging(level);
    }

    @Override
    protected Terminal createTerminal(final Branding branding) {
      return TerminalBuilder.builder().dumb(true).build()
    }

    @Override
    protected StreamSet createStreamSet(final Terminal terminal) {
      return new StreamSet(input, new PrintStream(out, true))
    }

    @Override
    protected Shell createShell(final IO io, final Variables variables, final Branding branding) {
      return mock(Shell.class)
    }

    @Override
    protected void exit(final int code) {
      this.exitCode = code
    }
  }
}
