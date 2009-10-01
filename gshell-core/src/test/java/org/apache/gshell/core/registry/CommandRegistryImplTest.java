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

package org.apache.gshell.core.registry;

import org.apache.gshell.command.CommandContext;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.registry.CommandRegistry;
import org.apache.gshell.testsupport.PlexusTestSupport;
import org.apache.gshell.core.registry.CommandRegistryImpl;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link CommandRegistryImpl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CommandRegistryImplTest
{
    private PlexusTestSupport plexus;

    private CommandRegistryImpl registry;

    @Before
    public void setUp() throws Exception {
        plexus = new PlexusTestSupport(this);
        registry = (CommandRegistryImpl)plexus.lookup(CommandRegistry.class);
    }

    @After
    public void tearDown() {
        registry = null;
        plexus.destroy();
        plexus = null;
    }

    @Test
    public void testRegisterCommandInvalid() throws Exception {
        try {
            registry.registerCommand(null, null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        try {
            registry.registerCommand("foo", null);
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }

        try {
            registry.registerCommand(null, new CommandActionSupport() {
                public Object execute(CommandContext context) throws Exception {
                    // ignore
                    return null;
                }
            });
            fail();
        }
        catch (AssertionError e) {
            // ignore
        }
    }
}