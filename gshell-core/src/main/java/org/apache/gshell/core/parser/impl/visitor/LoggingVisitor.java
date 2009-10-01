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

package org.apache.gshell.core.parser.impl.visitor;

import org.apache.gshell.core.parser.impl.ASTCommandLine;
import org.apache.gshell.core.parser.impl.ASTExpression;
import org.apache.gshell.core.parser.impl.ASTOpaqueString;
import org.apache.gshell.core.parser.impl.ASTPlainString;
import org.apache.gshell.core.parser.impl.ASTQuotedString;
import org.apache.gshell.core.parser.impl.ASTWhitespace;
import org.apache.gshell.core.parser.impl.ParserVisitor;
import org.apache.gshell.core.parser.impl.SimpleNode;
import org.apache.gshell.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor whichs logs nodes in the tree.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class LoggingVisitor
    implements ParserVisitor
{
    public static enum Level {
        INFO,
        DEBUG
    }

    private final Logger log;

    private final Level level;

    private int indent = 0;

    public LoggingVisitor() {
        this(LoggerFactory.getLogger(LoggingVisitor.class));
    }

    public LoggingVisitor(final Logger log) {
        this(log, Level.DEBUG);
    }

    public LoggingVisitor(final Logger log, final Level level) {
        assert log != null;
        assert level != null;

        this.log = log;
        this.level = level;
    }

    private Object log(final Class type, final SimpleNode node, Object data) {
        // Short-circuit of logging level does not match
        switch (level) {
            case INFO:
                if (!log.isInfoEnabled()) {
                    return data;
                }
                break;

            case DEBUG:
                if (!log.isDebugEnabled()) {
                    return data;
                }
                break;
        }

        StringBuilder buff = new StringBuilder(Strings.repeat(" ", indent));

        buff.append(node).append(" (").append(type.getName()).append(')');
        if (data != null) {
            buff.append("; Data: ").append(data);
        }

        switch (level) {
            case INFO:
                log.info(buff.toString());
                break;

            case DEBUG:
                log.debug(buff.toString());
                break;
        }

        indent++;
        data = node.childrenAccept(this, data);
        indent--;

        return data;
    }

    public Object visit(final SimpleNode node, Object data) {
        return log(SimpleNode.class, node, data);
    }

    public Object visit(final ASTCommandLine node, Object data) {
        return log(ASTCommandLine.class, node, data);
    }

    public Object visit(final ASTExpression node, Object data) {
        return log(ASTExpression.class, node, data);
    }

    public Object visit(final ASTWhitespace node, Object data) {
        return log(ASTWhitespace.class, node, data);
    }

    public Object visit(final ASTQuotedString node, Object data) {
        return log(ASTQuotedString.class, node, data);
    }

    public Object visit(final ASTOpaqueString node, Object data) {
        return log(ASTOpaqueString.class, node, data);
    }

    public Object visit(final ASTPlainString node, Object data) {
        return log(ASTPlainString.class, node, data);
    }
}