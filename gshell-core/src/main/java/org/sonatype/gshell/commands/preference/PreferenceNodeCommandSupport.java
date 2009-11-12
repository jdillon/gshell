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
import org.sonatype.gshell.util.cli.Argument;

import java.util.prefs.Preferences;

/**
 * ???
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command
public abstract class PreferenceNodeCommandSupport
    extends PreferenceCommandSupport
{
    @Argument(index = 0, required = true)
    private String path;

    protected Preferences node() throws Exception {
        Preferences root = root();
        log.debug("Root: {}", root);

        Preferences node = root.node(path);
        log.debug("Node: {}", node);

        return node;
    }
}
