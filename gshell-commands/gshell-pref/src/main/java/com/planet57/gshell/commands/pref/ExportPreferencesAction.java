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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.Flushables;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * Export preference nodes to a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "pref/export", description = "Export preferences")
@Preferences(path = "commands/pref/export")
public class ExportPreferencesAction
    extends PreferenceNodeActionSupport
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Exporting preferences to: %s")
    String exportingTo(File file);
  }

  private static final Messages messages = I18N.create(Messages.class);

  @Preference
  @Option(name = "t", longName = "subtree", description = "Include sub-tree preferences")
  private boolean subTree;

  @Nullable
  @Argument(index = 1, description = "Preferences export file", token = "FILE")
  private File file;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    java.util.prefs.Preferences prefs = node();

   try (OutputStream out = openStream(io)) {
      if (subTree) {
        prefs.exportSubtree(out);
      }
      else {
        prefs.exportNode(out);
      }

      Flushables.flushQuietly(out);
    }

    prefs.sync();

    return null;
  }

  private OutputStream openStream(final IO io) throws IOException {
    if (file == null) {
      return io.streams.out;
    }
    else {
      io.println(messages.exportingTo(file));
      return new BufferedOutputStream(new FileOutputStream(file));
    }
  }
}
