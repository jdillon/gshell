/*
 * Copyright (c) 2009-present the original author or authors.
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

import org.sonatype.gshell.command.resolver.Node;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

/**
 * {@link HelpPage} utilities.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpPageUtil
{
    public static void render(final PrintWriter out, final Collection<? extends HelpPage> pages) {
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

    public static Collection<HelpPage> pagesFor(final Node node, final HelpContentLoader loader) {
        assert node != null;
        assert loader != null;

        Collection<HelpPage> pages = new LinkedList<HelpPage>();
        for (Node child : node.children()) {
            pages.add(pageFor(child, loader));
        }

        return pages;
    }

    public static HelpPage pageFor(final Node node, final HelpContentLoader loader) {
        assert node != null;
        assert loader != null;

        if (node.isGroup()) {
            return new GroupHelpPage(node, loader);
        }

        return new CommandHelpPage(node, loader);
    }
}