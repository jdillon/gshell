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

package org.apache.maven.shell.cli;

import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

/**
 * Tests for the {@link Printer} class.
 *
 * @version $Rev$ $Date$
 */
public class PrinterTest
{
    TestBean bean;

    CommandLineProcessor clp;

    @Before
    public void setup() {
        bean = new TestBean();
        clp = new CommandLineProcessor(bean);
    }

    @After
    public void teardown() {
        bean = null;
        clp = null;
    }

    @Test
    public void test1() throws Exception {
        Printer printer = new Printer(clp);

        StringWriter out = new StringWriter();
        printer.printUsage(out);

        String tmp = out.getBuffer().toString();
        System.out.println(tmp);
        assertNotNull(tmp);
    }

    private static class TestBean
    {
        @Option(name="-1", aliases={"--foo", "-bar"}, description="this is a test")
        String a;

        @Option(name="-2", aliases={"--2", "-2"}, description="this is a really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really, really long description")
        String b;

        @Argument(description="these are arguments")
        List<String> args;
    }
}