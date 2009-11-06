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

package org.sonatype.gshell.util.setter;

import org.junit.Test;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.cli.CommandLineProcessorTestSupport;
import org.sonatype.gshell.cli.Option;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link org.sonatype.gshell.util.setter.MethodSetter} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MethodSetterTest
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
    public void test1() throws Exception {
        clp.process("-1", "test");

        assertEquals("test", bean.o);
    }

    @Test
    public void test2() throws Exception {
        clp.process("test");

        assertEquals("test", bean.a);
    }

    private static class TestBean
    {
        String o, a;

        @Option(name="-1")
        public void setOption(String value) {
            this.o = value;
        }

        @Argument
        public void setArgument(String value) {
            this.a = value;
        }
    }
}