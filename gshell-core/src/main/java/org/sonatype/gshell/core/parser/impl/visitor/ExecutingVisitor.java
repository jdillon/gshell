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

package org.sonatype.gshell.core.parser.impl.visitor;

import org.sonatype.gshell.Shell;
import org.sonatype.gshell.core.parser.impl.ASTCommandLine;
import org.sonatype.gshell.core.parser.impl.ASTExpression;
import org.sonatype.gshell.core.parser.impl.ASTOpaqueArgument;
import org.sonatype.gshell.core.parser.impl.ASTPlainArgument;
import org.sonatype.gshell.core.parser.impl.ASTQuotedArgument;
import org.sonatype.gshell.core.parser.impl.ASTWhitespace;
import org.sonatype.gshell.core.parser.impl.ParserVisitor;
import org.sonatype.gshell.core.parser.impl.SimpleNode;
import org.sonatype.gshell.core.parser.impl.eval.Evaluator;
import org.sonatype.gshell.core.parser.impl.eval.EvaluatorFactory;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.notification.ErrorNotification;
import org.sonatype.gshell.util.Arguments;
import org.sonatype.gshell.util.Strings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Visitor which will execute command-lines as parsed.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ExecutingVisitor
    implements ParserVisitor
{
    private final Shell shell;

    private final CommandExecutor executor;

    private final Evaluator evaluator;

    public ExecutingVisitor(final Shell shell, final CommandExecutor executor) {
        assert shell != null;
        assert executor != null;

        this.shell = shell;
        this.executor = executor;
        this.evaluator = EvaluatorFactory.get();
    }

    public Object visit(final SimpleNode node, final Object data) {
        assert node != null;

        // It is an error if we forgot to implement a node handler
        throw new Error("Unhandled node type: " + node.getClass().getName());
    }

    public Object visit(final ASTCommandLine node, final Object data) {
        assert node != null;

        // Visiting children will execute separate commands in serial
        List results = new LinkedList();
        node.childrenAccept(this, results);

        if (!results.isEmpty()) {
            return results.get(results.size() - 1);
        }
        return null;
    }

    public Object visit(final ASTExpression node, final Object data) {
        assert node != null;

        ExpressionState state = new ExpressionState(node);
        node.childrenAccept(this, state);

        Object[] args = state.getArguments();
        String path = String.valueOf(args[0]);
        args = Arguments.shift(args);

        Object result;
        try {
            result = executor.execute(shell, path, args);
        }
        catch (Exception e) {
            throw new ErrorNotification("Shell execution failed; path=" + path + "; args=" + Strings.join(args, ", "), e);
        }

        List results = (List) data;
        //noinspection unchecked
        results.add(result);

        return result;
    }

    public Object visit(final ASTWhitespace node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState) data;
        state.next();
        return data;
    }

    public Object visit(final ASTQuotedArgument node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState) data;
        String value = eval(node.getValue());
        return state.append(value);
    }

    public Object visit(final ASTPlainArgument node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState) data;
        String value = eval(node.getValue());
        return state.append(value);
    }

    public Object visit(final ASTOpaqueArgument node, final Object data) {
        assert node != null;

        ExpressionState state = (ExpressionState) data;
        return state.append(node.getValue());
    }

    private String eval(final String expression) {
        // expression could be null
        Object value;
        try {
            value = evaluator.eval(expression);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // FIXME: Need to return Object, but for now use String
        return String.valueOf(value);
    }

    //
    // ExpressionState
    //

    private static class ExpressionState
    {
        private final StringBuilder buff;

        private final List<Object> args;

        public ExpressionState(final ASTExpression root) {
            assert root != null;
            this.args = new ArrayList<Object>(root.jjtGetNumChildren());
            this.buff = new StringBuilder();
        }

        public String append(final String value) {
            assert value != null;
            buff.append(value);
            return value;
        }

        public void next() {
            // If there is something in the buffer, then add it as the next argument and reset the buffer
            if (buff.length() != 0) {
                args.add(buff.toString());
                buff.setLength(0);
            }
        }

        public Object[] getArguments() {
            // If there is something still on the buffer it is the last argument
            next();
            return args.toArray();
        }
    }
}
