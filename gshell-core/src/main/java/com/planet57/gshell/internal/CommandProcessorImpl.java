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
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandResolver;
import com.planet57.gshell.command.ExecuteAliasAction;
import com.planet57.gshell.command.Node;
import com.planet57.gshell.functions.Functions;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Function;
import org.apache.felix.service.threadio.ThreadIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

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

  @Nullable
  @Override
  protected Function getCommand(final String name, @Nullable final Object path) {
    assert name != null;

    // gogo commands resolve with "*:" syntax; if colon missing skip
    int colon = name.indexOf(':');
    if (colon < 0) {
      return null;
    }

    // strip off colon for resolution of gshell command actions
    CommandAction action = lookupAction(name.substring(colon + 1));
    if (action != null) {
      return new CommandActionFunction(action);
    }

    return super.getCommand(name, path);
  }

  @Nullable
  private CommandAction lookupAction(final String name) {
    log.debug("Lookup action: {}", name);

    CommandAction action = null;

    // first attempt to resolve alias
    try {
      String target = aliases.getAlias(name);
      action = new ExecuteAliasAction(name, target);
    }
    catch (AliasRegistry.NoSuchAliasException e) {
      // ignore
    }

    // then attempt to resolve node
    if (action == null) {
      Node node = resolver.resolve(name);
      if (node != null) {
        action = node.getAction();
      }
    }

    return action;
  }

  // TODO: consider how we want to generally cope with functions and the registry

  public void addFunctions(final Functions functions) {
    checkNotNull(functions);
    log.debug("Adding functions: {}", functions);

    Object target = functions.target();
    for (String name : functions.names()) {
      addCommand(null, target, name);
    }
  }

  public void removeFunctions(final Functions functions) {
    checkNotNull(functions);
    log.debug("Removing functions: {}", functions);

    Object target = functions.target();
    for (String name : functions.names()) {
      removeCommand(null, name, target);
    }
  }
}
