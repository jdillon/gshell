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

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

/**
 * Tests for the {@link Printer} class.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class PrinterTest
    extends ProcessorTestSupport
{
    private TestBean bean;

    protected Object createBean() {
        bean = new TestBean();
        return bean;
    }

    @Test
    public void testOptionsArgumentsSize() {
        assertOptionsArgumentsSize(2, 1);
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

        @Option(name="-2", aliases={"--2", "-2"}, description="this is a reallya, reallyb, reallyc, reallyd, reallye, reallyf, reallyg, reallyh, reallyi, reallyj, reallyk, reallyl, reallym, reallyn, reallyo, reallyp, reallyq, reallyr, reallys, reallyt, reallyu, reallyv, reallyw, reallyx long description")
        String b;

        @Argument(description="these are arguments")
        List<String> args;
    }
}