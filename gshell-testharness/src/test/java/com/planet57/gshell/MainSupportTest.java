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
package com.planet57.gshell;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.testharness.TestBranding;
import com.planet57.gshell.util.io.StreamSet;
import com.planet57.gshell.variables.Variables;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MainSupport}.
 */
public class MainSupportTest
  extends TestSupport
{
  private MockMain underTest;

  @Before
  public void setUp() throws Exception {
    Ansi.setEnabled(false);
    underTest = new MockMain();
  }

  @After
  public void tearDown() throws Exception {
    underTest = null;
  }

  @Test
  public void test_h() throws Exception {
    underTest.boot("-h");

    log(new String(underTest.out.toByteArray()));
    assertThat(underTest.exitCode, is(0));
  }

  @Test
  public void test__help() throws Exception {
    underTest.boot("--help");

    log(new String(underTest.out.toByteArray()));
    assertThat(underTest.exitCode, is(0));
  }

  @Test
  public void test__debug() throws Exception {
    underTest.boot("--debug");

    log(new String(underTest.out.toByteArray()));
    assertThat(underTest.exitCode, is(0));

    assertThat(System.getProperty("shell.logging.console.threshold"), is("DEBUG"));
  }

  private class MockMain
      extends MainSupport
  {
    public int exitCode;

    public ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);

    public ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Override
    protected Terminal createTerminal(Branding branding) throws Exception {
      return TerminalBuilder.builder().dumb(true).build();
    }

    @Override
    protected StreamSet createStreamSet() {
      return new StreamSet(in, new PrintStream(out, true));
    }

    @Override
    protected Branding createBranding() {
      return new TestBranding(util.resolveFile("target/shell-home"));
    }

    @Override
    protected Shell createShell(IO io, Variables variables, Branding branding) throws Exception {
      return mock(Shell.class);
    }

    @Override
    protected void exit(int code) {
      this.exitCode = code;
    }
  }
}
