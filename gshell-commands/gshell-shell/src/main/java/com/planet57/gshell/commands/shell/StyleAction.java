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
package com.planet57.gshell.commands.shell;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.style.MemoryStyleSource;
import com.planet57.gshell.util.style.Styler;

// HACK: primitive action to manage styles, not ideal and should be refined further

/**
 * Manage styles.
 *
 * @since 3.0
 */
@Command(name="style", description = "Manage styles")
public class StyleAction
    extends CommandActionSupport
{
  static {
    // HACK: for now adjust to use memory-source instead of default nop-source
    Styler.setSource(new MemoryStyleSource());
  }

  @Argument(index = 0, required = true, description = "Style group", token = "GROUP")
  private String group;

  @Argument(index = 1, required = true, description = "Style name", token = "NAME")
  private String name;

  @Nullable
  @Argument(index = 2, description = "Style specification", token = "SPEC")
  private String spec;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    // HACK:
    MemoryStyleSource source = (MemoryStyleSource)Styler.getSource();

    if (spec == null) {
      String spec = source.group(group).get(name);
      context.getIo().format("[%s] %s=%s", group, name, spec);
    }
    else {
      source.group(group).put(name, spec);
    }

    return null;
  }
}
