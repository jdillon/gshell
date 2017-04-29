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
package com.planet57.gshell.internal;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.NoSuchAliasException;
import com.planet57.gshell.command.AliasAction;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.variables.VariableNames;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.CommandSessionListener;
import org.apache.felix.service.command.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ???
 *
 * @since 3.0
 */
@Named
@Singleton
public class CommandProcessorImpl
  extends org.apache.felix.gogo.runtime.CommandProcessorImpl
{
  private static final Logger log = LoggerFactory.getLogger(CommandProcessorImpl.class);

  private final AliasRegistry aliases;

  private final CommandResolver resolver;

  @Inject
  public CommandProcessorImpl(final AliasRegistry aliases, final CommandResolver resolver) {
    this.aliases = checkNotNull(aliases);
    this.resolver = checkNotNull(resolver);

    addListener(new CommandSessionListener()
    {
      @Override
      public void beforeExecute(final CommandSession session, final CharSequence line) {
        if (line.length() == 0 || !log.isTraceEnabled()) {
          return;
        }

        StringBuilder hex = new StringBuilder();
        StringBuilder idx = new StringBuilder();

        line.chars().forEach(ch -> {
          hex.append('x').append(Integer.toHexString(ch)).append(' ');
          idx.append(' ').append((char) ch).append("  ");
        });

        log.trace("Execute: {}\n{}\n{}", line, hex, idx);
      }

      private void setLastResult(final CommandSession session, final Object result) {
        Shell shell = (Shell) session.get(CommandActionFunction.SHELL_VAR);
        shell.getVariables().set(VariableNames.LAST_RESULT, result);
        session.put(VariableNames.LAST_RESULT, result);
      }

      @Override
      public void afterExecute(final CommandSession session, final CharSequence line, final Object result) {
        if (line.length() == 0) {
          return;
        }

        if (log.isDebugEnabled()) {
          log.debug("Result: {}", String.valueOf(result)); // result could be throwable
        }

        setLastResult(session, result);
      }

      @Override
      public void afterExecute(final CommandSession session, final CharSequence line, final Exception exception) {
        if (line.length() == 0) {
          return;
        }

        log.debug("Exception", exception);

        setLastResult(session, exception);
      }
    });
  }

  @Override
  protected Function getCommand(final String name, final Object path) {
    assert name != null;
    // ignore path (ie. gogo scope)

    CommandAction action = null;
    if (aliases.containsAlias(name)) {
      try {
        action = new AliasAction(name, aliases.getAlias(name));
      }
      catch (NoSuchAliasException e) {
        // should never happen
        throw new Error();
      }
    }
    else {
      Node node = resolver.resolve(name);
      if (node != null) {
        action = node.getAction();
      }
    }

    if (action != null) {
      if (action instanceof CommandAction.Prototype) {
        action = ((CommandAction.Prototype)action).create();
      }

      return new CommandActionFunction(action);
    }

    return null;
  }
}
