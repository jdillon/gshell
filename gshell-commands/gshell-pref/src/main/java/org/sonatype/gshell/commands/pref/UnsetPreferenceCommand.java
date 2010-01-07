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

package org.sonatype.gshell.commands.pref;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.Argument;

import java.util.prefs.Preferences;

/**
 * Unset a preference value.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name="pref/unset")
public class UnsetPreferenceCommand
    extends PreferenceNodeCommandSupport
{
    @Argument(index = 1, required = true)
    private String key;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        log.debug("Unsetting preference: {}", key);

        Preferences prefs = node();
        prefs.remove(key);
        prefs.sync();
        
        return Result.SUCCESS;
    }
}