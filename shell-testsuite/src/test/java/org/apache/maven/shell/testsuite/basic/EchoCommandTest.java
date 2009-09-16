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

package org.apache.maven.shell.testsuite.basic;

import org.apache.maven.shell.testsuite.CommandTestSupport;
import org.junit.Test;

/**
 * Tests for the {@link EchoCommand}.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EchoCommandTest
    extends CommandTestSupport
{
    public EchoCommandTest() {
        super("echo");
    }

    @Test
    public void testEcho_a_b_c() throws Exception {
        Object result = executeWithArgs("a b c");
        assertEqualsSuccess(result);

        // FIXME: Need to verify the result
    }

    @Test
    public void testEcho_$mvnsh_home() throws Exception {
        Object result = executeWithArgs("${mvnsh.home}");
        assertEqualsSuccess(result);

        // FIXME: Need to verify the result
    }

    @Test
    public void testEchoWithStop() throws Exception {
        Object result = executeWithArgs("-- -D");
        assertEqualsSuccess(result);

        // FIXME: Need to verify the result
    }
}