/*
 * Copyright (c) 2009-2011 the original author or authors.
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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link org.sonatype.gshell.parser.impl.ParserVisitor} usage.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ParserVisitorTest
    extends ParserTestSupport
{
    @Test
    public void testVisitor1() throws Exception {
        String input = "a \"b\" 'c' d";

        ASTCommandLine cl = parse(input);

        MockCommandLineVisitor v = new MockCommandLineVisitor();

        Object result = cl.jjtAccept(v, null);

        assertNull(v.simpleNode);
        assertNotNull(v.commandLine);
        assertNotNull(v.expression);
        assertNotNull(v.quotedArgument);
        assertNotNull(v.opaqueArgument);
        assertNotNull(v.plainArgument);
        assertNotNull(v.whitespace);
    }

    private static class MockCommandLineVisitor
        implements ParserVisitor
    {
        private SimpleNode simpleNode;
        private ASTCommandLine commandLine;
        private ASTExpression expression;
        private ASTQuotedArgument quotedArgument;
        private ASTOpaqueArgument opaqueArgument;
        private ASTPlainArgument plainArgument;
        private ASTWhitespace whitespace;

        public Object visit(SimpleNode node, Object data) {
            this.simpleNode = node;

            return node.childrenAccept(this, data);
        }

        public Object visit(ASTCommandLine node, Object data) {
            this.commandLine = node;

            return node.childrenAccept(this, data);
        }

        public Object visit(ASTExpression node, Object data) {
            this.expression = node;

            return node.childrenAccept(this, data);
        }

        public Object visit(ASTWhitespace node, Object data) {
            this.whitespace = node;
            return node.childrenAccept(this, data);
        }

        public Object visit(ASTQuotedArgument node, Object data) {
            this.quotedArgument = node;

            return node.childrenAccept(this, data);
        }

        public Object visit(ASTOpaqueArgument node, Object data) {
            this.opaqueArgument = node;

            return node.childrenAccept(this, data);
        }

        public Object visit(ASTPlainArgument node, Object data) {
            this.plainArgument = node;

            return node.childrenAccept(this, data);
        }
    }
}