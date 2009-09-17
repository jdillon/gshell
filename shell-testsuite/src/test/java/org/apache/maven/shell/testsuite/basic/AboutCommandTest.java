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

import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.testsuite.CommandTestSupport;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for the {@link AboutCommand}.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AboutCommandTest
    extends CommandTestSupport
{
    public AboutCommandTest() {
        super("about");
    }

    @Test
    public void testTooManyArguments() throws Exception {
        try {
            executeWithArgs("1");
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }
}