/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.commands.preference;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.io.Flusher;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.util.cli.Option;
import org.sonatype.gshell.util.pref.Preference;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

/**
 * Export preference nodes to a file.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="pref/export")
public class ExportPreferencesCommand
    extends PreferenceNodeCommandSupport
{
    @Preference
    @Option(name = "-t", aliases = {"--subtree"})
    private boolean subTree;

    @Argument(index = 1)
    private File file;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        Preferences prefs = node();

        OutputStream out;
        if (file == null) {
            out = io.streams.out;
        }
        else {
            io.info("Exporting preferences to: {}", file); // TODO: i18n
            out = new BufferedOutputStream(new FileOutputStream(file));
        }

        try {
            if (subTree) {
                prefs.exportSubtree(out);
            }
            else {
                prefs.exportNode(out);
            }

            Flusher.flush(out);
        }
        finally {
            if (file != null) {
                Closer.close(out);
            }
        }

        prefs.sync();
        
        return Result.SUCCESS;
    }
}