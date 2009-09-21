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
 * Tests integer processing.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class IntegerTest
    extends ProcessorTestSupport
{
    private TestBean bean;

    protected Object createBean() {
        bean = new TestBean();
        return bean;
    }

    @Test
    public void test1() throws Exception {
        process("-1", "1");
        assertEquals(1, bean.n);
    }

    @Test
    public void test2() throws Exception {
        process("-2", "1");
        assertEquals(new Integer(1), bean.i);
    }

    @Test
    public void test3() throws Exception {
        process("-21");
        assertEquals(new Integer(1), bean.i);
    }

    private static class TestBean
    {
        @Option(opt="1", args=1)
        int n;

        @Option(opt="2", args=1)
        Integer i;
    }
}