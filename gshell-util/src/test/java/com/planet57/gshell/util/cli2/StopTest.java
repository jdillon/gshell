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
package com.planet57.gshell.util.cli2;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests options.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StopTest
    extends CliProcessorTestSupport
{
    private static class Simple
    {
        @Option(name = "h")
        boolean help;

        @Option(name = "v")
        boolean verbose;

        @Argument()
        List<String> remaining;
    }

    private Simple bean;

    @Override
    protected Object createBean() {
        return bean = new Simple();
    }

    @Test
    public void testStopOption() throws Exception {
        clp.process("-h", "--", "-v", "bar", "--baz");
        assertTrue(bean.help);
        assertEquals(3, bean.remaining.size());
        assertEquals("-v", bean.remaining.get(0));
        assertEquals("bar", bean.remaining.get(1));
        assertEquals("--baz", bean.remaining.get(2));
    }

    @Test
    public void testStopAtNonOption() throws Exception {
        clp.setStopAtNonOption(true);
        clp.process("-h", "foo", "-v", "bar", "--baz");
        assertTrue(bean.help);
        assertEquals(4, bean.remaining.size());
        assertEquals("foo", bean.remaining.get(0));
        assertEquals("-v", bean.remaining.get(1));
        assertEquals("bar", bean.remaining.get(2));
        assertEquals("--baz", bean.remaining.get(3));
    }
}