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

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Joiner;
import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.CliProcessorAware;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Define an alias or list defined aliases.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "alias", description = "Define an alias or list defined aliases.")
public class AliasAction
    extends CommandActionSupport
    implements CliProcessorAware
{
  private final AliasRegistry aliasRegistry;

  @Nullable
  @Argument(index = 0, description = "Name of the alias to define", token = "NAME")
  private String name;

  @Nullable
  @Argument(index = 1, description = "Target command to be aliased as NAME", token = "TARGET")
  private List<String> target;

  @Inject
  public AliasAction(final AliasRegistry aliasRegistry) {
    this.aliasRegistry = checkNotNull(aliasRegistry);
  }

  @Inject
  public AliasAction installCompleters(@Named("alias-name") final Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  public void setProcessor(final CliProcessor processor) {
    checkNotNull(processor);
    processor.setStopAtNonOption(true);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    if (name == null) {
      return listAliases(context);
    }

    return defineAlias(context);
  }

  private Object listAliases(final CommandContext context) throws Exception {
    IO io = context.getIo();

    log.debug("Listing defined aliases");

    Map<String, String> aliases = aliasRegistry.getAliases();

    if (aliases.isEmpty()) {
      io.out.println(getMessages().format("info.no-aliases"));
    }
    else {
      // Determine the maximum name length
      int maxNameLen = 0;
      for (String name : aliases.keySet()) {
        if (name.length() > maxNameLen) {
          maxNameLen = name.length();
        }
      }

      io.out.println(getMessages().format("info.defined-aliases"));
      String nameFormat = "%-" + maxNameLen + 's';

      for (Map.Entry<String, String> entry : aliases.entrySet()) {
        String formattedName = String.format(nameFormat, entry.getKey());
        io.out.format("  @|bold %s|@ ", formattedName);
        io.out.println(getMessages().format("info.alias-to", entry.getValue()));
      }
    }

    return null;
  }

  private Object defineAlias(final CommandContext context) throws Exception {
    checkArgument(target != null, getMessages().format("error.missing-arg", getMessages().getMessage("command.argument.target.token")));

    String alias = Joiner.on(" ").join(target);
    log.debug("Defining alias: {} -> {}", name, alias);

    aliasRegistry.registerAlias(name, alias);

    return null;
  }
}
