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
import org.sonatype.gshell.util.ansi.Ansi;
import org.sonatype.gshell.util.cli.Option;
import org.sonatype.gshell.util.pref.Preference;

import java.util.prefs.Preferences;

/**
 * List preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="pref/list")
public class ListPreferencesCommand
    extends PreferenceNodeCommandSupport
{
    @Preference
    @Option(name = "-r", aliases = {"--recursive"})
    private boolean recursive;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        list(io, node());

        node().sync();
        
        return Result.SUCCESS;
    }

    private void list(final IO io, final Preferences node) throws Exception {
        io.info("{}", Ansi.ansi().fg(Ansi.Color.GREEN).a(node.absolutePath()).reset());

        for (String key : node.keys()) {
            io.info("  {}: {}", Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(key).reset(), node.get(key, null));
        }
        if (recursive) {
            for (String name : node.childrenNames()) {
                list(io, node.node(name));
            }
        }
    }
}