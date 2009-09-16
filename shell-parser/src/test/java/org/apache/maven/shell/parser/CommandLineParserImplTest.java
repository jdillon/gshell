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
import org.codehaus.plexus.PlexusTestCase;

/**
 * Unit tests for the {@link CommandLineParserImpl} class.
 *
 * @version $Rev: 572187 $ $Date: 2007-09-03 06:19:30 +0700 (Mon, 03 Sep 2007) $
 */
public class CommandLineParserImplTest
    extends PlexusTestCase
{
    private CommandLineParserImpl clp;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        clp = (CommandLineParserImpl)lookup(CommandLineParser.class);
    }

    @Override
    protected void tearDown() throws Exception {
        clp = null;

        super.tearDown();
    }

    public void testParseNull() throws Exception {
        try {
            clp.parse(null);
            fail("Accepted null value");
        }
        catch (AssertionError expected) {
            // ignore
        }
    }
}