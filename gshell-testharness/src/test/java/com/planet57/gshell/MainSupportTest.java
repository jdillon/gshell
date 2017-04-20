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
import com.planet57.gshell.testharness.DummyShell;
import com.planet57.gshell.testharness.TestBranding;
import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestUtil;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link MainSupport}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MainSupportTest
{
  private final TestUtil util = new TestUtil(this);

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

  @Test
  public void test_h() throws Exception {
    main.boot("-h");
    Assert.assertEquals(ExitNotification.DEFAULT_CODE, main.exitCode);
  }

  @Test
  public void test__help() throws Exception {
    main.boot("--help");
    assertEquals(ExitNotification.DEFAULT_CODE, main.exitCode);
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
      return new DummyShell();
    }

    @Override
    protected void exit(int code) {
      this.exitCode = code;
    }
  }

}
