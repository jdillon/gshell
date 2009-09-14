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

package org.apache.maven.shell.parser.visitor;

import org.apache.maven.shell.ShellContext;
import org.apache.maven.shell.command.Arguments;
import org.apache.maven.shell.command.CommandExecutor;
import org.apache.maven.shell.notification.ErrorNotification;
import org.apache.maven.shell.parser.ASTCommandLine;
import org.apache.maven.shell.parser.ASTExpression;
import org.apache.maven.shell.parser.ASTOpaqueString;
import org.apache.maven.shell.parser.ASTPlainString;
import org.apache.maven.shell.parser.ASTQuotedString;
import org.apache.maven.shell.parser.ParserVisitor;
import org.apache.maven.shell.parser.SimpleNode;
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
 * @version $Rev$ $Date$
 */
public class ExecutingVisitor
    implements ParserVisitor
{
    private final ShellContext context;

    private final CommandExecutor executor;

    private final Interpolator interp = new StringSearchInterpolator("${", "}");

    public ExecutingVisitor(final ShellContext context, final CommandExecutor executor) {
        assert context != null;
        assert executor != null;

        this.context = context;
        this.executor = executor;

        interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        // interp.addValueSource(new VariablesValueSource());
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

        // Create the argument list (cmd name + args)
        List<Object> list = new ArrayList<Object>(node.jjtGetNumChildren());
        node.childrenAccept(this, list);

        Object[] args = list.toArray(new Object[list.size()]);
        assert list.size() >= 1;

        String path = String.valueOf(args[0]);
        args = Arguments.shift(args);

        Object result;
        try {
            result = executor.execute(context, path, args);
        }
        catch (Exception e) {
            throw new ErrorNotification("Shell execution failed; path=" + path + "; args=" + StringUtils.join(args, ", "), e);
        }

        List results  = (List)data;
        //noinspection unchecked
        results.add(result);

        return result;
    }

    @SuppressWarnings({"unchecked"})
    private Object appendString(final String value, final Object data) {
        assert data != null;
        assert data instanceof List;

        List<Object> args = (List<Object>)data;
        args.add(value);

        return value;
    }

    private String interpolate(final Object value) {
        // FIXME:
        // return interp.interpolate(value);
        return String.valueOf(value);
    }

    public Object visit(final ASTQuotedString node, final Object data) {
        assert node != null;

        String value = interpolate(node.getValue());

        return appendString(value, data);
    }

    public Object visit(final ASTPlainString node, final Object data) {
        assert node != null;

        String value = interpolate(node.getValue());

        return appendString(value, data);
    }

    public Object visit(final ASTOpaqueString node, final Object data) {
        assert node != null;

        return appendString(node.getValue(), data);
    }
}
