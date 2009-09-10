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

import org.apache.maven.shell.Command;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.CommandException;
import org.apache.maven.shell.notification.ExitNotification;
import org.apache.maven.shell.registry.CommandRegistry;

/**
 * Tests that the shell can boot up.
 *
 * @version $Rev$ $Date$
 */
public class AliasesTest
    extends PlexusTestSupport
{
    public void testDefineAliasExecute() throws Exception {
        CommandRegistry registry = lookup(CommandRegistry.class);
        assertNotNull(registry);
        registry.registerCommand(lookup(Command.class, "alias"));

        Shell shell = lookup(Shell.class);
        assertNotNull(shell);

        shell.execute("alias a=b");
        shell.execute("alias");

        try {
            shell.execute("a");
            fail();
        }
        catch (CommandException e) {
            // expected
        }
    }
}