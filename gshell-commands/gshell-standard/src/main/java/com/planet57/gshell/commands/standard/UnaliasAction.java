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
package com.planet57.gshell.commands.standard;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.jline.Complete;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Undefine an alias.
 *
 * @since 2.5
 */
@Command(name = "unalias", description = "Undefine an alias")
public class UnaliasAction
    extends CommandActionSupport
{
  private final AliasRegistry aliasRegistry;

  @Argument(index = 0, required = true, description = "Name of the alias to undefine.", token = "NAME")
  @Complete("alias-name")
  private String name;

  @Inject
  public UnaliasAction(final AliasRegistry aliasRegistry) {
    this.aliasRegistry = checkNotNull(aliasRegistry);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) {
    log.debug("Un-defining alias: {}", name);

    try {
      aliasRegistry.removeAlias(name);
    }
    catch (AliasRegistry.NoSuchAliasException e) {
      log.debug("Alias not defined: {}", name);
    }

    return null;
  }
}
