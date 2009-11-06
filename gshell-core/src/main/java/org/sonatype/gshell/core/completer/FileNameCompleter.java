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

package org.sonatype.gshell.core.completer;

import jline.console.Completer;
import org.sonatype.gshell.ShellHolder;
import org.sonatype.gshell.VariableNames;
import org.sonatype.gshell.Variables;

import java.io.File;

/**
 * {@link Completer} for file names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class FileNameCompleter
    extends jline.console.completers.FileNameCompleter
    implements Completer, VariableNames
{
    @Override
    protected File getUserHome() {
        Variables vars = ShellHolder.get().getVariables();
        return vars.get(SHELL_USER_HOME, File.class);
    }

    @Override
    protected File getUserDir() {
        Variables vars = ShellHolder.get().getVariables();
        return new File(vars.get(SHELL_USER_DIR, String.class));
    }

    @Override
    protected CharSequence render(final File file, CharSequence name) {
        assert file != null;
        assert name != null;

        // FIXME: This is still unhappy, even with AnsiString :-(
        //         Basically the problem is that what we want to display (ansi-encoced string)
        //         is different than what we want to be completed (non-ansi string)

        /*
        if (file.isDirectory()) {
            name = Ansi.ansi().fg(Ansi.Color.BLUE).a(name).a(File.separator).reset().toString();
        }
        else if (file.canExecute()) {
            name = Ansi.ansi().fg(Ansi.Color.GREEN).a(name).a("*").reset().toString();
        }

        if (file.isHidden()) {
            name = Ansi.ansi().a(Ansi.Attribute.INTENSITY_FAINT).a(name).reset().toString();
        }

        return new AnsiString(name);
        */

        return name;
    }
}