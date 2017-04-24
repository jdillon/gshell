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
package com.planet57.gshell.help;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.planet57.gshell.command.resolver.Node;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} utilities.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpPageUtil
{
  /**
   * Render a column-formatted index of help pages.
   */
  public static void renderIndex(final PrintWriter out, final Collection<? extends HelpPage> pages) {
    checkNotNull(out);
    checkNotNull(pages);

    // construct a printf format with sizing for showing columns
    int max = pages.stream().mapToInt(page -> page.getName().length()).max().getAsInt();
    String nameFormat = "%-" + max + 's';

    for (HelpPage page : pages) {
      String formattedName = String.format(nameFormat, page.getName());
      out.format("  @|bold %s|@", formattedName);

      String description = page.getDescription();
      if (description != null) {
        out.printf("   %s%n", description);
      }
      else {
        out.println();
      }
    }
  }

  public static Collection<HelpPage> pagesFor(final Node node, final HelpContentLoader loader) {
    checkNotNull(node);
    checkNotNull(loader);

    return node.children().stream().map(child -> pageFor(child, loader)).collect(Collectors.toList());
  }

  public static HelpPage pageFor(final Node node, final HelpContentLoader loader) {
    checkNotNull(node);
    checkNotNull(loader);

    if (node.isGroup()) {
      return new GroupHelpPage(node, loader);
    }

    return new CommandHelpPage(node, loader);
  }
}
