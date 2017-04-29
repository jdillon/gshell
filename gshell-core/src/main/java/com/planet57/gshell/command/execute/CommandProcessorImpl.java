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
package com.planet57.gshell.command.execute;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.NoSuchAliasException;
import com.planet57.gshell.command.AliasAction;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import org.apache.felix.service.command.Function;

import javax.annotation.Nullable;
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
  private final AliasRegistry aliases;

  private final CommandResolver resolver;

  @Inject
  public CommandProcessorImpl(final AliasRegistry aliases, final CommandResolver resolver) {
    this.aliases = checkNotNull(aliases);
    this.resolver = checkNotNull(resolver);
  }

  @Override
  protected Function getCommand(final String name, final Object path) {
    CommandAction action = createAction(name);
    if (action != null) {
      return new CommandActionFunction(action);
    }
    return null;
  }

  @Nullable
  private CommandAction createAction(final String name) {
    assert name != null;
    CommandAction action;
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
      if (node == null) {
        return null;
      }
      action = node.getAction();
    }

    if (action instanceof CommandAction.Prototype) {
      return ((CommandAction.Prototype)action).create();
    }
    return action;
  }
}
