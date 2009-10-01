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

package org.apache.gshell.core.parser;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.apache.gshell.execute.CommandLineParser;
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
    private CommandLineParser parser;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule() {
            @Override
            protected void configure() {
                bind(CommandLineParser.class).to(CommandLineParserImpl.class);
            }
        });
        parser = injector.getInstance(CommandLineParser.class);
    }

    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testParseNull() throws Exception {
        try {
            parser.parse(null);
            fail();
        }
        catch (AssertionError expected) {
            // ignore
        }
    }
}