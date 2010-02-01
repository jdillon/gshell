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

package org.sonatype.gshell.vars;

import jline.console.Completer;
import jline.console.completers.StringsCompleter;
import org.sonatype.gshell.shell.ShellHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * {@link Completer} for variable names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class VariableNameCompleter
    implements Completer
{
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        Variables vars = ShellHolder.get().getVariables();

        // There are no events for variables muck, so each time we have to rebuild the list.
        StringsCompleter delegate = new StringsCompleter();
        Collection<String> strings = delegate.getStrings();

        Iterator<String> iter = vars.names();
        while (iter.hasNext()) {
            String name = iter.next();
            strings.add(name);
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}