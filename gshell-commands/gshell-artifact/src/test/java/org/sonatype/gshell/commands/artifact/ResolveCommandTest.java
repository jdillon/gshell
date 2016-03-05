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
package org.sonatype.gshell.commands.artifact;

import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.gshell.command.support.CommandTestSupport;

/**
 * Tests for the {@link ResolveCommand}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResolveCommandTest
    extends CommandTestSupport
{
    public ResolveCommandTest() {
        super(ResolveCommand.class);
    }

    @Override
    @Test
    @Ignore
    public void testDefault() throws Exception {
        // nothing
    }

    @Ignore // FIXME:
    @Test
    public void test1() throws Exception {
        Object result = executeWithArgs("jline:line:0.9.94");
        assertEqualsSuccess(result);
    }
}