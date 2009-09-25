/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.testsuite;

import org.apache.gshell.Branding;
import org.apache.gshell.History;
import org.apache.gshell.Shell;
import org.apache.gshell.Variables;
import org.apache.gshell.ansi.Ansi;
import org.apache.gshell.core.MainSupport;
import org.apache.gshell.io.IO;
import org.apache.gshell.notification.ExitNotification;
import org.apache.gshell.testsupport.TestUtil;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

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
        assertEquals(ExitNotification.DEFAULT_CODE, main.exitCode);
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
        protected Branding createBranding() throws Exception {
            return new TestBranding(util.resolveFile("target/shell-home"));
        }

        @Override
        protected Shell createShell() throws Exception {
            return new MockShell();
        }

        @Override
        protected void exit(int code) {
            this.exitCode = code;
        }
    }

    private class MockShell
        implements Shell
    {
        @Override
        public Branding getBranding() {
            return null;
        }

        @Override
        public IO getIo() {
            return null;
        }

        @Override
        public Variables getVariables() {
            return null;
        }

        @Override
        public History getHistory() {
            return null;
        }

        @Override
        public boolean isOpened() {
            return false;
        }

        @Override
        public void close() {
        }

        @Override
        public Object execute(String line) throws Exception {
            return null;
        }

        @Override
        public Object execute(String command, Object[] args) throws Exception {
            return null;
        }

        @Override
        public Object execute(Object... args) throws Exception {
            return null;
        }

        @Override
        public boolean isInteractive() {
            return false;
        }

        @Override
        public void run(Object... args) throws Exception {
        }
    }
}