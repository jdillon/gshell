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
import com.planet57.gshell.notification.ExitNotification;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.testharness.TestBranding;
import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MainSupport}.
 */
public class MainSupportTest
  extends TestSupport
{
  private MockMain main;

  @Before
  public void setUp() throws Exception {
    Ansi.setEnabled(false);
    main = new MockMain();
  }

  @After
  public void tearDown() throws Exception {
    main = null;
  }

  // FIXME: due to how MainSupport works to setup streams; these tests will spit out to system.out even if surefire configured to redirect

  /**
   * All commands must support {@code -h}.
   */
  @Test
  public void test_h() throws Exception {
    main.boot("-h");
    assertThat(main.exitCode, is(ExitNotification.DEFAULT_CODE));
  }

  /**
   * All commands must support {@code --help}.
   */
  @Test
  public void test__help() throws Exception {
    main.boot("--help");
    assertThat(main.exitCode, is(ExitNotification.DEFAULT_CODE));
  }

  private class MockMain
      extends MainSupport
  {
    public int exitCode;

    @Override
    protected Branding createBranding() {
      return new TestBranding(util.resolveFile("target/shell-home"));
    }

    @Override
    protected Shell createShell() throws Exception {
      return mock(Shell.class);
    }

    @Override
    protected void exit(int code) {
      this.exitCode = code;
    }
  }
}
