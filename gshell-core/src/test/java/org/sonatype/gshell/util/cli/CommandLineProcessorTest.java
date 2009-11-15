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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the {@link org.sonatype.gshell.util.cli.CommandLineProcessor} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CommandLineProcessorTest
    extends CommandLineProcessorTestSupport
{
    private TestBean bean;

    @Override
    protected Object createBean() {
        bean = new TestBean();
        return bean;
    }

    @Test
    public void testOptionsArgumentsSize() {
        assertOptionsArgumentsSize(1, 1);
    }

    @Test
    public void testStopAtNonOption() throws Exception {
        clp.setStopAtNonOption(true);
        clp.process("-1", "test", "foo", "-bar", "baz");

        assertEquals("test", bean.s);
        assertNotNull(bean.args);
        assertEquals(3, bean.args.size());
        assertEquals("foo", bean.args.get(0));
        assertEquals("-bar", bean.args.get(1));
        assertEquals("baz", bean.args.get(2));

    }

    private static class TestBean
    {
        @Option(name="-1")
        String s;

        @Argument
        List<String> args;
    }
}