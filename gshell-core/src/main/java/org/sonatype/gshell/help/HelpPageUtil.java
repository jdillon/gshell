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
import org.sonatype.gshell.command.GroupAction;
import org.sonatype.gshell.command.resolver.Node;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * {@link HelpPage} utilities.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpPageUtil
{
    public static void renderPages(final PrintWriter out, final Collection<? extends HelpPage> pages) {
        assert out != null;
        assert pages != null;

        int max = 0;
        for (HelpPage page : pages) {
            int len = page.getName().length();
            max = Math.max(len, max);
        }
        String nameFormat = "%-" + max + 's';

        for (HelpPage page : pages) {
            String formattedName = String.format(nameFormat, page.getName());
            out.format("  @|bold %s|@", formattedName);

            String description = page.getDescription();
            if (description != null) {
                out.print("  ");
                out.println(description);
            }
            else {
                out.println();
            }
        }
    }

    public static HelpPage pageForNode(final Node node, final HelpContentLoader loader) {
        assert node != null;
        assert loader != null;

        CommandAction action = node.getAction();
        if (action instanceof GroupAction) {
            return new GroupHelpPage((GroupAction)action, node.children(), loader);
        }
        else {
            return new CommandHelpPage(action, loader);
        }
    }
}