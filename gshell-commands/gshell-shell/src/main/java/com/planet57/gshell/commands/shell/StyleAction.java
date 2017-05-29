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

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.util.style.StyleSource;
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
  @Nullable
  @Argument(index = 0, description = "Style group", token = "GROUP")
  private String group;

  @Nullable
  @Argument(index = 1, description = "Style name", token = "NAME")
  private String name;

  @Nullable
  @Argument(index = 2, description = "Style specification", token = "SPEC")
  private String spec;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    StyleSource source = Styler.getSource();
    IO io = context.getIo();

    if (group == null) {
      // display all styles in all groups
      Iterator<String> iter = source.groups().iterator();
      if (!iter.hasNext()) {
        io.println("No styles defined");
        return null;
      }

      while (iter.hasNext()) {
        String group = iter.next();
        displayGroup(io, group, source.styles(group));
        if (iter.hasNext()) {
          io.println();
        }
      }
    }
    else if (name == null) {
      // displays styles in group
      displayGroup(io, group, source.styles(group));
    }
    else if (spec == null) {
      // display specific style
      String spec = source.styles(group).get(name);
      if (spec == null) {
        io.println("Style not defined");
      }
      else {
        io.format("@{fg:green [%s]} @{bold %s}: %s%n", group, name, spec);
      }
    }
    else {
      // set a style
      source.styles(group).put(name, spec);
    }

    return null;
  }

  private void displayGroup(final IO io, final String group, final Map<String,String> styles) {
    io.format("@{fg:green [%s]}%n", group);
    if (styles.isEmpty()) {
      io.println("  No styles defined");
    }
    else {
      styles.forEach((key, value) -> io.format("  @{bold %s}: %s%n", key, value));
    }
  }
}
