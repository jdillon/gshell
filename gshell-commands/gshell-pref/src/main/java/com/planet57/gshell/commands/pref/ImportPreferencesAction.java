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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.prefs.Preferences;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import javax.annotation.Nonnull;

/**
 * Import preference nodes from a file.
 *
 * @since 2.0
 */
@Command(name = "pref/import", description = "Import preferences")
public class ImportPreferencesAction
    extends PreferenceActionSupport
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Importing preferences from: %s")
    String importingFrom(File file);
  }

  private static final Messages messages = I18N.create(Messages.class);

  @Argument(index = 0, required = true, description = "Preferences file to import", token = "FILE")
  private File source;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    io.println(messages.importingFrom(source));

    try (InputStream in = new BufferedInputStream(new FileInputStream(source))) {
      Preferences.importPreferences(in);
    }
    return null;
  }
}
