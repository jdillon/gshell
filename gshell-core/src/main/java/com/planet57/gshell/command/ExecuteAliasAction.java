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
package com.planet57.gshell.command;

import com.google.common.base.Joiner;
import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.util.cli2.OpaqueArguments;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandAction} to execute an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class ExecuteAliasAction
  extends ComponentSupport
  implements CommandAction, OpaqueArguments
{
  private final String name;

  private final String target;

  public ExecuteAliasAction(final String name, final String target) {
    this.name = checkNotNull(name);
    this.target = checkNotNull(target);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSimpleName() {
    return name;
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    String alias = target;

    // append any additional arguments
    List<?> args = context.getArguments();
    if (args.size() > 0) {
      alias = String.format("%s %s", target, Joiner.on(" ").join(args));
    }

    log.debug("Executing alias ({}) -> {}", getName(), alias);

    return context.getShell().execute(alias);
  }

  @Override
  public String toString() {
    return "ExecuteAliasAction{" +
      "name='" + name + '\'' +
      ", target='" + target + '\'' +
      '}';
  }
}
