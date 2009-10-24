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

package org.sonatype.gshell.cli.handler;

import static org.junit.Assert.*;
import org.junit.Test;
import org.sonatype.gshell.cli.Option;
import org.sonatype.gshell.cli.ProcessorTestSupport;

/**
 * Tests for the {@link org.sonatype.gshell.cli.handler.BooleanHandler} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class BooleanHandlerTest
    extends ProcessorTestSupport
{
    private TestBean bean;

    @Override
    protected Object createBean() {
        bean = new TestBean();
        return bean;
    }

    @Test
    public void testOptionsArgumentsSize() {
        assertOptionsArgumentsSize(2, 0);
    }

    @Test
    public void test1a() throws Exception {
        clp.process("-1");

        assertTrue(bean.flag);
    }

    @Test
    public void test1b() throws Exception {
        clp.process("--one");

        assertTrue(bean.flag);
    }

    @Test
    public void test2() throws Exception {
        clp.process("-2", "false");

        assertFalse(bean.flag2);

        clp.process("-2", "true");

        assertTrue(bean.flag2);
    }

    @Test
    public void test3a() throws Exception {
        clp.process("-2=false");

        assertFalse(bean.flag2);

        clp.process("-2=true");

        assertTrue(bean.flag2);
    }

    @Test
    public void test3b() throws Exception {
        clp.process("--two", "false");

        assertFalse(bean.flag2);

        clp.process("--two", "true");

        assertTrue(bean.flag2);
    }

    @Test
    public void test3c() throws Exception {
        clp.process("--two=false");

        assertFalse(bean.flag2);

        clp.process("--two=true");

        assertTrue(bean.flag2);
    }

    private static class TestBean
    {
        @Option(name="-1", aliases={"--one"})
        boolean flag;

        @Option(name="-2", aliases={"--two"}, argumentRequired=true)
        boolean flag2;
    }
}