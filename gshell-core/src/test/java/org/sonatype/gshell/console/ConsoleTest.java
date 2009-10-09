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

package org.sonatype.gshell.console;

import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests for the {@link org.apache.gshell.console.Console} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConsoleTest
{
    @Test
    public void testConstructorArguments() {
        try {
            new MockConsole(null);
            fail();
        }
        catch (AssertionError e) {
            // expected
        }

        new MockConsole(new MockExecutor());
    }

    //
    // MockExecutor
    //

    private static class MockExecutor
        implements Console.Executor
    {
        public Console.Result result;

        public Console.Result execute(final String line) throws Exception {
            return result;
        }
    }

    //
    // MockPrompter
    //

    private static class MockPrompter
        implements Console.Prompter
    {
        public String prompt;

        public String prompt() {
            return prompt;
        }
    }

    //
    // MockConsole
    //
    
    private static class MockConsole
        extends Console
    {
        public String line;

        private MockConsole(final Executor executor) {
            super(executor);
        }

        @Override
        protected String readLine(final String prompt) throws IOException {
            return line;
        }
    }
}