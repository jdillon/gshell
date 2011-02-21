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
package org.sonatype.gshell.commands.pref;

import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.util.cli2.Option;

import java.util.prefs.Preferences;

/**
 * Support for preference commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class PreferenceCommandSupport
    extends CommandActionSupport
{
    @Option(name = "s", longName="system")
    private boolean system;

    protected Preferences root() {
        Preferences root;
        if (system) {
            root = Preferences.systemRoot();
        }
        else {
            root = Preferences.userRoot();
        }
        return root;
    }
}