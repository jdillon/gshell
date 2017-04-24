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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.planet57.gshell.execute.CommandExecutor;
import com.planet57.gshell.execute.ErrorNotification;
import com.planet57.gshell.parser.impl.ASTCommandLine;
import com.planet57.gshell.parser.impl.ASTExpression;
import com.planet57.gshell.parser.impl.ASTOpaqueArgument;
import com.planet57.gshell.parser.impl.ASTPlainArgument;
import com.planet57.gshell.parser.impl.ASTQuotedArgument;
import com.planet57.gshell.parser.impl.ASTWhitespace;
import com.planet57.gshell.parser.impl.ParserVisitor;
import com.planet57.gshell.parser.impl.SimpleNode;
import com.planet57.gshell.parser.impl.eval.Evaluator;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.Arguments;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Visitor which will execute command-lines as parsed.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ExecutingVisitor
    implements ParserVisitor
{
  private final Shell shell;

  private final CommandExecutor executor;

  private final Evaluator evaluator;

  public ExecutingVisitor(final Shell shell, final CommandExecutor executor, final Evaluator evaluator) {
    this.shell = checkNotNull(shell);
    this.executor = checkNotNull(executor);
    this.evaluator = checkNotNull(evaluator);
  }

  @Override
  public Object visit(final SimpleNode node, final Object data) {
    assert node != null;

    // It is an error if we forgot to implement a node handler
    throw new Error("Unhandled node type: " + node.getClass().getName());
  }

  @Override
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

  @Override
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
      throw new ErrorNotification("Shell execution failed; path=" + path + "; args=" + Joiner.on(", ").join(args), e);
    }

    List results = (List) data;
    //noinspection unchecked
    results.add(result);

    return result;
  }

  @Override
  public Object visit(final ASTWhitespace node, final Object data) {
    assert node != null;
    assert data != null;

    ExpressionState state = (ExpressionState) data;
    state.next();
    return data;
  }

  @Override
  public Object visit(final ASTQuotedArgument node, final Object data) {
    assert node != null;
    assert data != null;

    ExpressionState state = (ExpressionState) data;
    String value = eval(node.getValue());
    return state.append(value);
  }

  @Override
  public Object visit(final ASTPlainArgument node, final Object data) {
    assert node != null;
    assert data != null;

    ExpressionState state = (ExpressionState) data;
    String value = eval(node.getValue());
    return state.append(value);
  }

  @Override
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

    // FIXME: Need to return Object, but for now use String; implies changes to ExpressionState and likely more to convert to Object
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
      checkNotNull(root);
      this.args = new ArrayList<>(root.jjtGetNumChildren());
      this.buff = new StringBuilder();
    }

    public String append(final String value) {
      checkNotNull(value);
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
