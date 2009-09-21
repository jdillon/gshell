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

package org.apache.maven.shell.cli2;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link String} processing.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StringTest
    extends ProcessorTestSupport
{
    private TestBean bean;

    protected Object createBean() {
        bean = new TestBean();
        return bean;
    }

    @Test
    public void test1() throws Exception {
        process("-1", "test");
        assertEquals("test", bean.one);
    }

    @Test
    public void test2a() throws Exception {
        process("-1test");
        assertEquals("test", bean.one);
    }

    @Test
    public void test2b() throws Exception {
        process("-1foo=bar");
        assertEquals("foo=bar", bean.one);
    }

    @Test
    public void test3() throws Exception {
        process("--one", "test");
        assertEquals("test", bean.one);
    }

    @Test
    public void test4() throws Exception {
        process("--one=test");
        assertEquals("test", bean.one);
    }

    @Test
    public void test5a() throws Exception {
        process("--one=foo=bar");
        assertEquals("foo=bar", bean.one);
    }

    @Test
    public void test5b() throws Exception {
        process("--one", "foo=bar");
        assertEquals("foo=bar", bean.one);
    }

    @Test
    public void test6() throws Exception {
        process("-2", "test");
        assertEquals("test", bean.two);
    }

    @Test
    public void test7() throws Exception {
        process("-2test");
        assertEquals("test", bean.two);
    }

    /*
    FIXME: Handle no args setting
    @Test
    public void test8() throws Exception {
        process("-3");
    }
    */

    /*
    FIXME: Handle collections
    @Test
    public void test9() throws Exception {
        process("-4", "a", "b", "c");
        assertEquals("abc", bean.four);
    }
    */

    private static class TestBean
    {
        @Option(opt="1", longOpt="one", args =1)
        String one;

        @Option(opt="2", args=1)
        String two;

        @Option(opt="3")
        String three;

        @Option(opt="4", args=-2) // Option.UNLIMITED
        String four;
    }
}