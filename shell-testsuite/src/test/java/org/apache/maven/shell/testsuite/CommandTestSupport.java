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

package org.apache.maven.shell.testsuite;

import org.apache.maven.shell.command.Command;

/**
 * Support for testing {@link Command} instances.
 *
 * @version $Rev$ $Date$
 */
public abstract class CommandTestSupport
    extends ShellTestSupport
{
    protected final String name;

    protected CommandTestSupport(final String name) {
        assertNotNull(name);
        this.name = name;
    }

    protected Object executeWithArgs(final String args) throws Exception {
        assertNotNull(args);
        return execute(name + " " + args);
    }

    protected void assertEqualsSuccess(final Object result) {
        assertEquals(Command.Result.SUCCESS, result);
    }

    protected void assertEqualsFailure(final Object result) {
        assertEquals(Command.Result.FAILURE, result);
    }

    public void testHelp() throws Exception {
        Object result;

        result = executeWithArgs("--help");
        assertEqualsSuccess(result);

        result = executeWithArgs("-h");
        assertEqualsSuccess(result);
    }

    public void testDefault() throws Exception {
        Object result = execute(name);
        assertEqualsSuccess(result);
    }
}