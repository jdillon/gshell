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

package org.apache.gshell.parser.impl.visitor;

import org.apache.gshell.Arguments;
import org.apache.gshell.Shell;
import org.apache.gshell.ShellHolder;
import org.apache.gshell.Variables;
import org.apache.gshell.execute.CommandExecutor;
import org.apache.gshell.notification.ErrorNotification;
import org.apache.gshell.parser.impl.ASTCommandLine;
import org.apache.gshell.parser.impl.ASTExpression;
import org.apache.gshell.parser.impl.ASTOpaqueString;
import org.apache.gshell.parser.impl.ASTPlainString;
import org.apache.gshell.parser.impl.ASTQuotedString;
import org.apache.gshell.parser.impl.ASTWhitespace;
import org.apache.gshell.parser.impl.ParserVisitor;
import org.apache.gshell.parser.impl.SimpleNode;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.util.StringUtils;

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

    private Interpolator interp;

    public ExecutingVisitor(final Shell shell, final CommandExecutor executor) {
        assert shell != null;
        assert executor != null;

        this.shell = shell;
        this.executor = executor;
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
            throw new ErrorNotification("Shell execution failed; path=" + path + "; args=" + StringUtils.join(args, ", "), e);
        }

        List results = (List)data;
        //noinspection unchecked
        results.add(result);

        return result;
    }

    public Object visit(final ASTWhitespace node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState)data;
        state.next();
        return data;
    }

    public Object visit(final ASTQuotedString node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState)data;
        String value = interpolate(node.getValue());
        return state.append(value);
    }

    public Object visit(final ASTPlainString node, final Object data) {
        assert node != null;
        assert data != null;

        ExpressionState state = (ExpressionState)data;
        String value = interpolate(node.getValue());
        return state.append(value);
    }

    private String interpolate(final String value) {
        assert value != null;

        // Skip interpolation if there is no start token
        if (!value.contains(StringSearchInterpolator.DEFAULT_START_EXPR)) {
            return value;
        }

        if (interp == null) {
            interp = new StringSearchInterpolator();
            interp.addValueSource(new AbstractValueSource(false) {
                public Object getValue(final String expression) {
                    Variables vars = ShellHolder.get().getVariables();
                    return vars.get(expression);
                }
            });
            interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        }

        try {
            return interp.interpolate(value);
        }
        catch (InterpolationException e) {
            throw new RuntimeException(e);
        }
    }

    public Object visit(final ASTOpaqueString node, final Object data) {
        assert node != null;

        ExpressionState state = (ExpressionState)data;
        return state.append(node.getValue());
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
