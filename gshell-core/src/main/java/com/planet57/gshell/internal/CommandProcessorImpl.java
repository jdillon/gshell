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
import com.planet57.gshell.command.ExecuteAliasAction;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Function;
import org.apache.felix.service.threadio.ThreadIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GShell adaption of GOGO {@link CommandProcessor}.
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
  public CommandProcessorImpl(@Nullable final ThreadIO threadIO, final AliasRegistry aliases, final CommandResolver resolver) {
    super(threadIO);
    log.debug("Thread-IO: {}", threadIO);

    this.aliases = checkNotNull(aliases);
    this.resolver = checkNotNull(resolver);
  }

  @Override
  protected Function getCommand(final String name, final Object path) {
    assert name != null;
    // ignore path (ie. gogo scope)

    CommandAction action = null;
    if (aliases.containsAlias(name)) {
      try {
        action = new ExecuteAliasAction(name, aliases.getAlias(name));
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

    return super.getCommand(name, path);
  }

  // TODO: consider how we want to generally cope with functions and the registry

  public void registerFunction(final Object target, final String... functions) {
    checkNotNull(target);
    checkNotNull(functions);
    checkArgument(functions.length > 0);

    Arrays.stream(functions).forEach(function -> {
      addCommand(null, target, function);
    });
  }
}
