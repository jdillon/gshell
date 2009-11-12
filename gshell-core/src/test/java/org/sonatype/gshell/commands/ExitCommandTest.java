/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.commands;

import org.junit.Test;
import org.sonatype.gshell.commands.ExitCommand;
import org.sonatype.gshell.notification.ExitNotification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the {@link ExitCommand}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ExitCommandTest
    extends CommandTestSupport
{
    public ExitCommandTest() {
        super("exit", ExitCommand.class);
    }

    @Override
    @Test
    public void testDefault() throws Exception {
        try {
            execute();
            fail();
        }
        catch (ExitNotification n) {
            assertEquals(ExitNotification.DEFAULT_CODE, n.code);
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
    public void testExitWithCode() throws Exception {
        try {
            executeWithArgs("57");
            fail();
        }
        catch (ExitNotification n) {
            assertEquals(57, n.code);
        }
    }

    @Test
    public void testExitWithInvalidCode() throws Exception {
        try {
            executeWithArgs("foo");
            fail();
        }
        catch (Exception e) {
            // expected
        }
    }
}