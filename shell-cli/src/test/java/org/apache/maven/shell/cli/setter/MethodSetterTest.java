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

package org.apache.maven.shell.cli.setter;

import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.cli.Option;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link MethodSetter} class.
 *
 * @version $Rev$ $Date$
 */
public class MethodSetterTest
{
    TestBean bean;

    CommandLineProcessor clp;

    @Before
    public void setup() {
        bean = new TestBean();
        clp = new CommandLineProcessor(bean);

        assertEquals(1, clp.getOptionHandlers().size());
        assertEquals(1, clp.getArgumentHandlers().size());
    }

    @After
    public void teardown() {
        bean = null;
        clp = null;
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