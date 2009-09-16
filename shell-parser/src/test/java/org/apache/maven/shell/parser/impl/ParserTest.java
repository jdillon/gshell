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

package org.apache.maven.shell.parser.impl;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

/**
 * Unit tests for the {@link Parser} class.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ParserTest
{
    private ASTCommandLine parse(final String input) throws ParseException {
        assertNotNull(input);

        Reader reader = new StringReader(input);
        Parser parser = new Parser();
        ASTCommandLine cl = parser.parse(reader);

        assertNotNull(cl);

        return cl;
    }

    public void testParseNull() throws Exception {
        try {
            new Parser().parse(null);
            fail();
        }
        catch (AssertionError e) {
            // expected
        }
    }
    
    //
    // Comments
    //

    @Test
    public void testSingleComment1() throws Exception {
        String input = "# this should be completly ignored";

        ASTCommandLine cl = parse(input);

        assertEquals(0, cl.jjtGetNumChildren());
    }

    @Test
    public void testSingleComment2() throws Exception {
        String input = "####";

        ASTCommandLine cl = parse(input);

        assertEquals(0, cl.jjtGetNumChildren());
    }

    @Test
    public void testSingleComment3() throws Exception {
        String input = "# ignored; this too";

        ASTCommandLine cl = parse(input);

        assertEquals(0, cl.jjtGetNumChildren());
    }

    //
    // Strings
    //

    @Test
    public void testStrings1() throws Exception {
        String input = "a b c";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        // 3 plain strings
        Node child = cl.jjtGetChild(0);
        assertEquals(3, child.jjtGetNumChildren());

        for (int i=0; i<3; i++ ) {
            Node node = child.jjtGetChild(i);
            assertEquals(ASTPlainString.class, node.getClass());
        }

        assertEquals("a", ((ASTPlainString)child.jjtGetChild(0)).getValue());
        assertEquals("b", ((ASTPlainString)child.jjtGetChild(1)).getValue());
        assertEquals("c", ((ASTPlainString)child.jjtGetChild(2)).getValue());
    }

    @Test
    public void testStrings2() throws Exception {
        String input = "a -b --c d";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        // 4 plain strings
        Node child = cl.jjtGetChild(0);
        assertEquals(4, child.jjtGetNumChildren());

        for (int i=0; i<4; i++ ) {
            Node node = child.jjtGetChild(i);
            assertEquals(ASTPlainString.class, node.getClass());
        }

        assertEquals("a", ((ASTPlainString)child.jjtGetChild(0)).getValue());
        assertEquals("-b", ((ASTPlainString)child.jjtGetChild(1)).getValue());
        assertEquals("--c", ((ASTPlainString)child.jjtGetChild(2)).getValue());
        assertEquals("d", ((ASTPlainString)child.jjtGetChild(3)).getValue());
    }

    @Test
    public void testQuotedStrings1() throws Exception {
        String input = "a \"b -c\" d";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        Node child = cl.jjtGetChild(0);
        assertEquals(3, child.jjtGetNumChildren());

        // Verify 2 plain strings + 1 quoted
        Node node;

        node = child.jjtGetChild(0);
        assertEquals(ASTPlainString.class, node.getClass());
        assertEquals("a", ((StringSupport)node).getValue());

        node = child.jjtGetChild(1);
        assertEquals(ASTQuotedString.class, node.getClass());
        assertEquals("b -c", ((StringSupport)node).getValue());
        assertEquals("\"b -c\"", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(2);
        assertEquals(ASTPlainString.class, node.getClass());
        assertEquals("d", ((StringSupport)node).getValue());
    }

    @Test
    public void testOpaqueStrings1() throws Exception {
        String input = "a 'b -c' d";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        Node child = cl.jjtGetChild(0);
        assertEquals(3, child.jjtGetNumChildren());

        // Verify 2 plain strings + 1 opaque
        Node node;

        node = child.jjtGetChild(0);
        assertEquals(ASTPlainString.class, node.getClass());
        assertEquals("a", ((StringSupport)node).getValue());

        node = child.jjtGetChild(1);
        assertEquals(ASTOpaqueString.class, node.getClass());
        assertEquals("b -c", ((StringSupport)node).getValue());
        assertEquals("'b -c'", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(2);
        assertEquals(ASTPlainString.class, node.getClass());
    }

    @Test
    public void testMoreStrings1() throws Exception {
        String input = "a 'b -c' \"d\" e";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        Node child = cl.jjtGetChild(0);
        assertEquals(4, child.jjtGetNumChildren());

        Node node;

        node = child.jjtGetChild(0);
        assertEquals(ASTPlainString.class, node.getClass());
        assertEquals("a", ((StringSupport)node).getValue());

        node = child.jjtGetChild(1);
        assertEquals(ASTOpaqueString.class, node.getClass());
        assertEquals("b -c", ((StringSupport)node).getValue());
        assertEquals("'b -c'", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(2);
        assertEquals(ASTQuotedString.class, node.getClass());
        assertEquals("d", ((StringSupport)node).getValue());
        assertEquals("\"d\"", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(3);
        assertEquals(ASTPlainString.class, node.getClass());
    }

    @Test
    public void testMoreStrings2() throws Exception {
        String input = "a \"b -c\" 'd' e";

        ASTCommandLine cl = parse(input);

        // One expression
        assertEquals(1, cl.jjtGetNumChildren());

        Node child = cl.jjtGetChild(0);
        assertEquals(4, child.jjtGetNumChildren());

        Node node;

        node = child.jjtGetChild(0);
        assertEquals(ASTPlainString.class, node.getClass());
        assertEquals("a", ((StringSupport)node).getValue());

        node = child.jjtGetChild(1);
        assertEquals(ASTQuotedString.class, node.getClass());
        assertEquals("b -c", ((StringSupport)node).getValue());
        assertEquals("\"b -c\"", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(2);
        assertEquals(ASTOpaqueString.class, node.getClass());
        assertEquals("d", ((StringSupport)node).getValue());
        assertEquals("'d'", ((StringSupport)node).getToken().image);

        node = child.jjtGetChild(3);
        assertEquals(ASTPlainString.class, node.getClass());
    }

    //
    // Compound
    //

    @Test
    public void testCompoundCommandLine1() throws Exception {
        String input = "a b c; d e f";

        ASTCommandLine cl = parse(input);

        assertEquals(2, cl.jjtGetNumChildren());

        //
        // TODO: Verify 2 expressions
        //
    }

    @Test
    public void testCompoundCommandLine2() throws Exception {
        String input = "a b c;";

        ASTCommandLine cl = parse(input);

        assertEquals(1, cl.jjtGetNumChildren());

        //
        // TODO: Verify ...
        //
    }

    @Test
    public void testCompoundCommandLine3() throws Exception {
        String input = "a b c;;;;";

        ASTCommandLine cl = parse(input);

        assertEquals(1, cl.jjtGetNumChildren());

        //
        // TODO: Verify ...
        //
    }

    @Test
    public void testCompoundCommandLine4() throws Exception {
        String input = "a b c;;;;d e f";

        ASTCommandLine cl = parse(input);

        assertEquals(2, cl.jjtGetNumChildren());

        //
        // TODO: Verify ...
        //
    }

    @Test
    public void testNotCompoundCommandLine1() throws Exception {
        String input = "a b c\\; d e f";

        ASTCommandLine cl = parse(input);

        assertEquals(1, cl.jjtGetNumChildren());

        //
        // TODO: Verify 1 expression
        //
    }
}