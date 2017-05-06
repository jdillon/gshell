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
package com.planet57.gshell.commands.pref;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;

import javax.annotation.Nonnull;

/**
 * List preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "pref/list", description = "List preferences")
@Preferences(path = "commands/pref/list")
public class ListPreferencesAction
    extends PreferenceNodeActionSupport
{
  @Preference
  @Option(name = "r", longName = "recursive", description = "Recursively list preferences")
  private boolean recursive;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    list(io, node());
    node().sync();
    return null;
  }

  private void list(final IO io, final java.util.prefs.Preferences node) throws Exception {
    io.format("@|green %s|@%n", node.absolutePath());

    for (String key : node.keys()) {
      io.format("  @|bold %s|@: %s%n", key, node.get(key, null));
    }

    if (recursive) {
      for (String name : node.childrenNames()) {
        list(io, node.node(name));
      }
    }
  }
}
