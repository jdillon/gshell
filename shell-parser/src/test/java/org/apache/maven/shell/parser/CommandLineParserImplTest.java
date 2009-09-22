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

package org.apache.maven.shell.parser;

import org.apache.maven.shell.command.CommandLineParser;
import org.apache.maven.shell.testsupport.PlexusTestSupport;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link CommandLineParserImpl} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CommandLineParserImplTest
{
    private PlexusTestSupport plexus;

    private CommandLineParserImpl clp;

    @Before
    public void setUp() throws Exception {
        plexus = new PlexusTestSupport(this);
        clp = (CommandLineParserImpl)plexus.lookup(CommandLineParser.class);
    }

    @After
    public void tearDown() {
        clp = null;
        plexus.destroy();
        plexus = null;
    }

    @Test
    public void testParseNull() throws Exception {
        try {
            clp.parse(null);
            fail();
        }
        catch (AssertionError expected) {
            // ignore
        }
    }
}