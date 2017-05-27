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
package com.planet57.gshell.commands.standard;

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.help.AliasHelpPage;
import com.planet57.gshell.help.CommandHelpPage;
import com.planet57.gshell.help.GroupHelpPage;
import com.planet57.gshell.help.HelpPage;
import com.planet57.gshell.help.HelpPageManager;
import com.planet57.gshell.help.HelpPageUtil;
import com.planet57.gshell.help.MetaHelpPage;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.jline.Complete;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display help pages.
 *
 * @since 2.5
 */
@Command(name = "help", description = "Display help pages")
@Preferences(path = "commands/help")
public class HelpAction
    extends CommandActionSupport
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Available pages:")
    String availablePages();

    @DefaultMessage("Matching pages:")
    String matchingPages();

    @DefaultMessage("No help page available for @|bold %s|@.  Try @|bold help|@ for a list of available pages.")
    String helpNotFound(String page);
  }

  private static final Messages messages = I18N.create(Messages.class);

  private final HelpPageManager helpPages;

  // TODO: maybe use an enum here to say; --include groups,commands,aliases (exclude meta) etc...

  @Preference
  @Option(name = "c", longName = "include-commands", description = "Include command pages", optionalArg = true)
  private Boolean includeCommands = true;

  @Preference
  @Option(name = "a", longName = "include-aliases", description = "Include alias pages", optionalArg = true)
  private Boolean includeAliases = true;

  @Preference
  @Option(name = "g", longName = "include-groups", description = "Include group pages", optionalArg = true)
  private Boolean includeGroups = true;

  @Preference
  @Option(name = "m", longName = "include-meta", description = "Include meta pages", optionalArg = true)
  private Boolean includeMeta = true;

  @Preference
  @Option(name = "A", longName = "include-all", description = "Include all pages", optionalArg = true)
  private Boolean includeAll;

  @Argument(description = "Display the help page for NAME or list pages matching NAME", token = "NAME")
  @Complete("help-page-name")
  private String name;

  @Inject
  public HelpAction(final HelpPageManager helpPages) {
    this.helpPages = checkNotNull(helpPages);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    // If there is no argument given, display all help pages in context
    if (name == null) {
      displayAvailable(context);
      return null;
    }

    // First try a direct match
    HelpPage page = helpPages.getPage(name);

    // if not direct match, then look for similar pages
    if (page == null) {
      Collection<HelpPage> pages = helpPages.getPages(query(
        it -> it != null && (it.getName().contains(name) || it.getDescription().contains(name))
      ));

      if (pages.size() == 1) {
        // if there is only one match, treat as a direct match
        page = pages.iterator().next();
      }
      else if (pages.size() > 1) {
        // else show matching pages
        io.println(messages.matchingPages());
        HelpPageUtil.renderIndex(io.out, pages);
        return null;
      }
    }

    // if not page matched, complain
    if (page == null) {
      io.println(messages.helpNotFound(name));
      return 1;
    }

    page.render(context.getShell(), io.out);

    return null;
  }

  private void displayAvailable(final CommandContext context) {
    Collection<HelpPage> pages = helpPages.getPages(query(helpPage -> true));
    IO io = context.getIo();
    io.println(messages.availablePages());
    HelpPageUtil.renderIndex(io.out, pages);
  }

  // FIXME: this and the --include-all doesn't seem to be very happy, redefine one one queries different types of pages

  private Predicate<HelpPage> query(final Predicate<HelpPage> predicate) {
    Predicate<HelpPage> query = predicate;

    if (includeAll == null || !includeAll) {
      if (includeAliases != null && !includeAliases) {
        query = query.and(TypePredicate.of(AliasHelpPage.class).negate());
      }
      if (includeMeta != null && !includeMeta) {
        query = query.and(TypePredicate.of(MetaHelpPage.class).negate());
      }
      if (includeCommands != null && !includeCommands) {
        query = query.and(TypePredicate.of(CommandHelpPage.class).negate());
      }
      if (includeGroups != null && !includeGroups) {
        query = query.and(TypePredicate.of(GroupHelpPage.class).negate());
      }
    }

    return query;
  }

  //
  // TypePredicate
  //

  private static class TypePredicate<T>
    implements Predicate<T>
  {
    private final Class<?> type;

    private TypePredicate(final Class<?> type) {
      this.type = checkNotNull(type);
    }

    @Override
    public boolean test(final T value) {
      return value != null && type.isAssignableFrom(value.getClass());
    }

    public static <T> Predicate<T> of(final Class<?> type) {
      return new TypePredicate<>(type);
    }
  }
}
