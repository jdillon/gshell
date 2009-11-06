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

package org.sonatype.gshell.util.cli;

import org.junit.After;
import org.junit.Before;
import org.sonatype.gshell.util.cli.CommandLineProcessor;

import static org.junit.Assert.assertEquals;

/**
 * Support for {@link CommandLineProcessor} tests.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CommandLineProcessorTestSupport
{
    protected CommandLineProcessor clp;

    @Before
    public void setUp() {
        clp = new CommandLineProcessor(createBean());
    }

    @After
    public void tearDown() {
        clp = null;
    }

    protected abstract Object createBean();

    protected void assertOptionsArgumentsSize(final int expectedOptions, final int expectedArguments) {
        assertEquals(expectedOptions, clp.getOptionHandlers().size());
        assertEquals(expectedArguments, clp.getArgumentHandlers().size());
    }
}