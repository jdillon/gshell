/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell.parser.impl;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for the {@link com.planet57.gshell.parser.impl.Parser} class.
 */
public class ParserTest
    extends ParserTestSupport
{
  @Test
  public void testParseNull() throws Exception {
    try {
      new Parser().parse(null);
      fail();
    }
    catch (AssertionError e) {
      // expected
    }
  }

  @Test
  public void testParseEOF() throws Exception {
    parse("");
  }

  @Test
  @Ignore("This should work but out parser is retarded")
  public void testParseSemiColon() throws Exception {
    parse(";");
  }

  //
  // Comments
  //

  @Test
  public void testSingleComment1() throws Exception {
    String input = "# ignore";

    ASTCommandLine cl = parse(input);

    // No expressions
    assertEquals(0, cl.jjtGetNumChildren());
  }

  @Test
  public void testSingleComment2() throws Exception {
    String input = "####";

    ASTCommandLine cl = parse(input);

    // No expressions
    assertEquals(0, cl.jjtGetNumChildren());
  }

  @Test
  public void testSingleComment3() throws Exception {
    String input = "# ignored; this too";

    ASTCommandLine cl = parse(input);

    // No expressions
    assertEquals(0, cl.jjtGetNumChildren());
  }

  //
  // Strings
  //

  @Test
  public void testStrings1() throws Exception {
    String input = "a b c";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 plain strings 2 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace

        for (int i=0; i<3; i++ ) {
            Node node = child.jjtGetChild(i);
            assertEquals(ASTPlainString.class, node.getClass());
        }

        assertEquals("a", ((ASTPlainString)child.jjtGetChild(0)).getValue());
        assertEquals("b", ((ASTPlainString)child.jjtGetChild(1)).getValue());
        assertEquals("c", ((ASTPlainString)child.jjtGetChild(2)).getValue());
        */
  }

  @Test
  public void testStrings2() throws Exception {
    String input = "a -b --c d";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 4 plain strings, 3 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(7, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace

        for (int i=0; i<4; i++ ) {
            Node node = child.jjtGetChild(i);
            assertEquals(ASTPlainString.class, node.getClass());
        }

        assertEquals("a", ((ASTPlainString)child.jjtGetChild(0)).getValue());
        assertEquals("-b", ((ASTPlainString)child.jjtGetChild(1)).getValue());
        assertEquals("--c", ((ASTPlainString)child.jjtGetChild(2)).getValue());
        assertEquals("d", ((ASTPlainString)child.jjtGetChild(3)).getValue());
        */
  }

  @Test
  public void testQuotedStrings1() throws Exception {
    String input = "a \"b -c\" d";

    ASTCommandLine cl = parse(input);

    // One expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 strings 2 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace
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
        */
  }

  @Test
  public void testOpaqueStrings1() throws Exception {
    String input = "a 'b -c' d";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 strings 2 whietspace
    Node child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace

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
        */
  }

  @Test
  public void testMoreStrings1() throws Exception {
    String input = "a 'b -c' \"d\" e";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 4 strings 3 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(7, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace

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
        */
  }

  @Test
  public void testMoreStrings2() throws Exception {
    String input = "a \"b -c\" 'd' e";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 4 strings, 3 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(7, child.jjtGetNumChildren());

        /*
        FIXME: Re-implement verification to know about whitespace

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
        */
  }

  //
  // Compound
  //

  @Test
  public void testCompoundCommandLine1a() throws Exception {
    String input = "a b c; d e f";

    ASTCommandLine cl = parse(input);

    // 2 expressions
    assertEquals(2, cl.jjtGetNumChildren());

    Node child;

    // 3 strings, 2 whitespace
    child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

    // 3 strings, 3 whitespace
    child = cl.jjtGetChild(1);
    assertEquals(6, child.jjtGetNumChildren());
  }

  @Test
  public void testCompoundCommandLine1b() throws Exception {
    String input = "a b c;d e f";

    ASTCommandLine cl = parse(input);

    // 2 expressions
    assertEquals(2, cl.jjtGetNumChildren());

    Node child;

    // 3 strings, 2 whitespace
    child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

    // 3 strings, 2 whitespace
    child = cl.jjtGetChild(1);
    assertEquals(5, child.jjtGetNumChildren());
  }

  @Test
  public void testCompoundCommandLine2() throws Exception {
    String input = "a b c;";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 strings, 2 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());
  }

  @Test
  public void testCompoundCommandLine3() throws Exception {
    String input = "a b c;;;;";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 strings, 2 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());
  }

  @Test
  public void testCompoundCommandLine4() throws Exception {
    String input = "a b c;;;;d e f";

    ASTCommandLine cl = parse(input);

    // 2 expressions
    assertEquals(2, cl.jjtGetNumChildren());

    Node child;

    // 3 strings, 2 whitespace
    child = cl.jjtGetChild(0);
    assertEquals(5, child.jjtGetNumChildren());

    // 3 strings, 2 whitespace
    child = cl.jjtGetChild(1);
    assertEquals(5, child.jjtGetNumChildren());
  }

  @Test
  public void testNotCompoundCommandLine1() throws Exception {
    String input = "a b c\\; d e f";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 6 strings, 5 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(11, child.jjtGetNumChildren());
  }

  @Test
  public void testQuotesQuotes() throws Exception {
    String input = "echo \"hi there\"\"yo\"";

    ASTCommandLine cl = parse(input);

    // 1 expression
    assertEquals(1, cl.jjtGetNumChildren());

    // 3 strings, 1 whitespace
    Node child = cl.jjtGetChild(0);
    assertEquals(4, child.jjtGetNumChildren());
  }
}
