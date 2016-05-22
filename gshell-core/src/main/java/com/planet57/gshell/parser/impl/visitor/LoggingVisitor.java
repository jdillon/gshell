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
package com.planet57.gshell.parser.impl.visitor;

import com.planet57.gshell.parser.impl.ASTCommandLine;
import com.planet57.gshell.parser.impl.ASTExpression;
import com.planet57.gshell.parser.impl.ASTOpaqueArgument;
import com.planet57.gshell.parser.impl.ASTPlainArgument;
import com.planet57.gshell.parser.impl.ASTQuotedArgument;
import com.planet57.gshell.parser.impl.ASTWhitespace;
import com.planet57.gshell.parser.impl.ParserVisitor;
import com.planet57.gshell.parser.impl.SimpleNode;
import com.planet57.gshell.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs nodes in the tree.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class LoggingVisitor
    implements ParserVisitor
{
    public static enum Level
    {
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

    public Object visit(final ASTQuotedArgument node, Object data) {
        return log(ASTQuotedArgument.class, node, data);
    }

    public Object visit(final ASTOpaqueArgument node, Object data) {
        return log(ASTOpaqueArgument.class, node, data);
    }

    public Object visit(final ASTPlainArgument node, Object data) {
        return log(ASTPlainArgument.class, node, data);
    }
}