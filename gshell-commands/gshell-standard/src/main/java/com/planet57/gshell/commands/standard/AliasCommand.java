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

import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.Strings;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.CliProcessorAware;
import jline.console.completer.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Define an alias or list defined aliases.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "alias")
public class AliasCommand
    extends CommandActionSupport
    implements CliProcessorAware
{
  private final AliasRegistry aliasRegistry;

  @Argument(index = 0)
  private String name;

  @Argument(index = 1)
  private List<String> target;

  @Inject
  public AliasCommand(final AliasRegistry aliasRegistry) {
    this.aliasRegistry = checkNotNull(aliasRegistry);
  }

  @Inject
  public AliasCommand installCompleters(@Named("alias-name") final Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  public void setProcessor(final CliProcessor processor) {
    checkNotNull(processor);
    processor.setStopAtNonOption(true);
  }

  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);

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
      io.println(getMessages().format("info.no-aliases"));
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

    return Result.SUCCESS;
  }

  private Object defineAlias(final CommandContext context) throws Exception {
    IO io = context.getIo();

    if (target == null) {
      io.error(getMessages().format("error.missing-arg"), getMessages().getMessage("command.argument.target.token"));
      return Result.FAILURE;
    }

    String alias = Strings.join(target.toArray(), " ");

    log.debug("Defining alias: {} -> {}", name, alias);

    aliasRegistry.registerAlias(name, alias);

    return Result.SUCCESS;
  }
}
