/*
 * Copyright (C) 2009 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.gshell.parser.impl;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Support for parser testing.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ParserTestSupport
{
    protected ASTCommandLine parse(final String input) throws ParseException {
        assertNotNull(input);

        Reader reader = new StringReader(input);
        Parser parser = new Parser();
        ASTCommandLine cl = parser.parse(reader);

        assertNotNull(cl);

        System.out.println("Parsed (" + input + "):");
        cl.dump("  ");

        return cl;
    }
}