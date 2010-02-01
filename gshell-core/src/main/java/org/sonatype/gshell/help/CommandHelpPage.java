/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.help;

import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.help.CommandHelpRenderer;

import static org.sonatype.gshell.command.CommandDocumenter.*;


import java.io.PrintWriter;

/**
 * {@link HelpPage} for a command.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandHelpPage
    implements HelpPage
{
    private final CommandAction command;

    private final CommandHelpRenderer renderer;

    public CommandHelpPage(final CommandAction command, final CommandHelpRenderer renderer) {
        assert command != null;
        this.command = command;
        assert renderer != null;
        this.renderer = renderer;
    }

    public String getName() {
        return command.getName();
    }

    public String getBriefDescription() {
        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }

    public void render(final PrintWriter out) {
        assert out != null;

        renderer.render(command, out);
    }
}