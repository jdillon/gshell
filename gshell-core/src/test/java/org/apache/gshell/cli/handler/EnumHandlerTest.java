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

package org.apache.gshell.cli.handler;

import org.apache.gshell.cli.Option;
import org.apache.gshell.cli.ProcessingException;
import org.apache.gshell.cli.ProcessorTestSupport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for the {@link org.apache.gshell.cli.handler.EnumHandler} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EnumHandlerTest
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
        assertOptionsArgumentsSize(1, 0);
    }

    @Test
    public void test1() throws Exception {
        clp.process("-t", "A");

        assertEquals(Type.A, bean.type);
    }

    @Test
    public void test2() throws Exception {
        clp.process("-t", "b");

        assertEquals(Type.B, bean.type);
    }

    @Test
    public void test3() throws Exception {
        try {
            clp.process("-t", "c");
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }

    @Test
    public void test4() throws Exception {
        clp.process("-t=b");

        assertEquals(Type.B, bean.type);
    }

    private static enum Type
    {
        A,
        B
    }

    private static class TestBean
    {
        @Option(name="-t")
        Type type;
    }
}