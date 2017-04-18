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

import javax.inject.Inject;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.NoSuchAliasException;
import com.planet57.gshell.command.AliasAction;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.registry.NoSuchCommandException;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.command.support.CommandHelpSupport;
import com.planet57.gshell.command.support.CommandPreferenceSupport;
import com.planet57.gshell.notification.ErrorNotification;
import com.planet57.gshell.notification.ResultNotification;
import com.planet57.gshell.parser.CommandLineParser;
import com.planet57.gshell.parser.CommandLineParser.CommandLine;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.Arguments;
import com.planet57.gshell.util.Strings;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.util.io.StreamJack;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The default {@link CommandExecutor} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandExecutorImpl
    implements CommandExecutor
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final AliasRegistry aliases;

  private final CommandResolver resolver;

  private final CommandLineParser parser;

  @Inject
  public CommandExecutorImpl(final AliasRegistry aliases, final CommandResolver resolver,
                             final CommandLineParser parser)
  {
    assert aliases != null;
    this.aliases = aliases;
    assert resolver != null;
    this.resolver = resolver;
    assert parser != null;
    this.parser = parser;
  }

  @Override
  public Object execute(final Shell shell, final String line) throws Exception {
    assert shell != null;
    assert line != null;

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
      // Decode the error notification
      Throwable cause = n.getCause();

      if (cause instanceof Exception) {
        throw (Exception) cause;
      }
      else if (cause instanceof Error) {
        throw (Error) cause;
      }
      else {
        throw n;
      }
    }
    finally {
      ShellHolder.set(lastShell);
    }
  }

  @Override
  public Object execute(final Shell shell, final Object... args) throws Exception {
    assert shell != null;
    assert args != null;

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
    // Always return a clone, original instance is a prototype
    return action.clone();
  }

  @Override
  public Object execute(final Shell shell, final String name, final Object[] args) throws Exception {
    assert shell != null;
    assert name != null;
    assert args != null;

    log.debug("Executing ({}): [{}]", name, Strings.join(args, ", "));

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
      PreferenceProcessor pp = CommandPreferenceSupport.createProcessor(action);
      pp.process();

      // Process command arguments unless marked as opaque
      if (!(action instanceof OpaqueArguments)) {
        CommandHelpSupport help = new CommandHelpSupport();
        CliProcessor clp = help.createProcessor(action);
        clp.process(Arguments.toStringArray(args));

        // Render command-line usage
        if (help.displayHelp) {
          io.out.println(CommandHelpSupport.getDescription(action));
          io.out.println();

          HelpPrinter printer = new HelpPrinter(clp);
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
            public Shell getShell() {
              return shell;
            }

            public Object[] getArguments() {
              return args;
            }

            public IO getIo() {
              return io;
            }

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

    log.debug("Result: {}", result);

    return result;
  }
}
