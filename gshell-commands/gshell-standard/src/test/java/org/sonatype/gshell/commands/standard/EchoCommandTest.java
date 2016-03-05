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
package org.sonatype.gshell.commands.standard;

import org.junit.Test;
import org.sonatype.gshell.command.support.CommandTestSupport;

/**
 * Tests for the {@link EchoCommand}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EchoCommandTest
    extends CommandTestSupport
{
    public EchoCommandTest() {
        super(EchoCommand.class);
    }
    
    private static final String NEWLINE = System.getProperty("line.separator");

    @Test
    public void testEcho_a_b_c() throws Exception {
        Object result = executeWithArgs("a b c");
        assertEqualsSuccess(result);
        assertOutputEquals("a b c" + NEWLINE);
    }

    @Test
    public void testEcho_$shell_home() throws Exception {
        Object result = executeWithArgs("${shell.home}");
        assertEqualsSuccess(result);
        assertOutputEquals(getShell().getVariables().get("shell.home") + NEWLINE);
    }

    @Test
    public void testEchoWithStop() throws Exception {
        Object result = executeWithArgs("-- -D");
        assertEqualsSuccess(result);
        assertOutputEquals("-D" + NEWLINE);
    }

    @Test
    public void testEchoWithSpacePadding() throws Exception {
        Object result = executeWithArgs("' foo '");
        assertEqualsSuccess(result);
        assertOutputEquals(" foo " + NEWLINE);
    }

    @Test
    public void testEchoNoNewline() throws Exception {
        Object result = executeWithArgs("-n foo");
        assertEqualsSuccess(result);
        assertOutputEquals("foo");
    }
}