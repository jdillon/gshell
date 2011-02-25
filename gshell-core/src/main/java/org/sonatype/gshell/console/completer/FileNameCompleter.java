/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.console.completer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import jline.console.completer.Completer;
import org.sonatype.gshell.variables.Variables;

import java.io.File;

import static org.sonatype.gshell.variables.VariableNames.*;

/**
 * {@link jline.console.completer.Completer} for file names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Singleton
public class FileNameCompleter
    extends jline.console.completer.FileNameCompleter
    implements Completer
{
    private final Provider<Variables> variables;

    @Inject
    public FileNameCompleter(final Provider<Variables> variables) {
        assert variables != null;
        this.variables = variables;
    }

    @Override
    protected File getUserHome() {
        return variables.get().get(SHELL_USER_HOME, File.class);
    }

    @Override
    protected File getUserDir() {
        Variables vars = variables.get();
        Object tmp = vars.get(SHELL_USER_DIR);
        assert tmp != null;
        
        if (tmp instanceof File) {
            return (File)tmp;
        }

        return new File(String.valueOf(tmp));
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