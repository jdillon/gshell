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
package com.planet57.gshell.execute;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.NoSuchAliasException;
import com.planet57.gshell.command.AliasAction;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.NoSuchCommandException;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.command.CommandHelper;
import com.planet57.gshell.parser.CommandLineParser;
import com.planet57.gshell.parser.CommandLineParser.CommandLine;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.Arguments;
import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.slf4j.MDC;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CommandExecutor} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class CommandExecutorImpl
  extends ComponentSupport
  implements CommandExecutor
{
  private final AliasRegistry aliases;

  private final CommandResolver resolver;

  private final CommandLineParser parser;

  @Inject
  public CommandExecutorImpl(final AliasRegistry aliases, final CommandResolver resolver,
                             final CommandLineParser parser)
  {
    this.aliases = checkNotNull(aliases);
    this.resolver = checkNotNull(resolver);
    this.parser = checkNotNull(parser);
  }

  @Override
  @Nullable
  public Object execute(final Shell shell, final String line) throws Exception {
    checkNotNull(shell);
    checkNotNull(line);

    if (line.trim().length() == 0) {
      log.trace("Ignoring empty line");
      return null;
    }

    final Shell lastShell = ShellHolder.set(shell);
    CommandLine cl = parser.parse(line);

    try {
      return cl.execute(shell, this);
    }
    catch (ErrorNotification n) {
      Throwable cause = n.getCause();
      Throwables.propagateIfPossible(cause, Exception.class, Error.class);
      // should normally never happen
      throw n;
    }
    finally {
      ShellHolder.set(lastShell);
    }
  }

  @Override
  @Nullable
  public Object execute(final Shell shell, final Object... args) throws Exception {
    checkNotNull(shell);
    checkNotNull(args);

    return execute(shell, String.valueOf(args[0]), Arguments.shift(args));
  }

  private CommandAction createAction(final String name) throws NoSuchAliasException, NoSuchCommandException {
    assert name != null;
    CommandAction action;
    if (aliases.containsAlias(name)) {
      action = new AliasAction(name, aliases.getAlias(name));
    }
    else {
      Node node = resolver.resolve(name);
      if (node == null) {
        throw new NoSuchCommandException(name);
      }
      action = node.getAction();
    }

    if (action instanceof CommandAction.Prototype) {
      return ((CommandAction.Prototype)action).create();
    }
    return action;
  }

  @Override
  @Nullable
  public Object execute(final Shell shell, final String name, final Object[] args) throws Exception {
    checkNotNull(shell);
    checkNotNull(name);
    checkNotNull(args);

    if (log.isDebugEnabled()) {
      log.debug("Executing ({}): {}", name, Arrays.asList(args));
    }

    Stopwatch watch = Stopwatch.createStarted();

    final CommandAction action = createAction(name);
    MDC.put(CommandAction.class.getName(), name);

    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final Shell lastShell = ShellHolder.set(shell);
    final IO io = shell.getIo();

    StreamJack.maybeInstall(io.streams);

    Object result = null;
    try {
      boolean execute = true;

      // Process command preferences
      PreferenceProcessor pp = CommandHelper.createPreferenceProcessor(action);
      pp.process();

      // Process command arguments unless marked as opaque
      if (!(action instanceof OpaqueArguments)) {
        CommandHelper help = new CommandHelper();
        CliProcessor clp = help.createCliProcessor(action);
        clp.process(Arguments.toStringArray(args));

        // Render command-line usage
        if (help.displayHelp) {
          io.out.println(CommandHelper.getDescription(action));
          io.out.println();

          HelpPrinter printer = new HelpPrinter(clp, io.terminal);
          printer.printUsage(io.out, action.getSimpleName());

          // Skip execution
          result = CommandAction.Result.SUCCESS;
          execute = false;
        }
      }

      if (execute) {
        try {
          result = action.execute(new CommandContext()
          {
            @Override
            public Shell getShell() {
              return shell;
            }

            @Override
            public Object[] getArguments() {
              return args;
            }

            @Override
            public IO getIo() {
              return io;
            }

            @Override
            public Variables getVariables() {
              return shell.getVariables();
            }
          });
        }
        catch (ResultNotification n) {
          result = n.getResult();
        }
      }
    }
    finally {
      io.flush();
      StreamJack.deregister();
      ShellHolder.set(lastShell);
      Thread.currentThread().setContextClassLoader(cl);
      MDC.remove(CommandAction.class.getName());
    }

    shell.getVariables().set(VariableNames.LAST_RESULT, result);

    log.debug("Result: {}; {}", result, watch);

    return result;
  }
}
