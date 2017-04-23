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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Predicate;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
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
import com.planet57.gshell.util.jline.InputStreamSource;
import com.planet57.gshell.util.predicate.PredicateBuilder;
import com.planet57.gshell.util.predicate.TypePredicate;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;
import org.fusesource.jansi.AnsiRenderWriter;
import org.jline.builtins.Less;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.AggregateCompleter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display help pages.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "help")
@Preferences(path = "commands/help")
public class HelpAction
    extends CommandActionSupport
{
  private final HelpPageManager helpPages;

  @Preference
  @Option(longName = "pager", optionalArg = true)
  private Boolean pager = true;

  // TODO: maybe use an enum here to say; --include groups,commands,aliases (exclude meta) etc...

  @Preference
  @Option(name = "c", longName = "include-commands", optionalArg = true)
  private Boolean includeCommands = true;

  @Preference
  @Option(name = "a", longName = "include-aliases", optionalArg = true)
  private Boolean includeAliases = true;

  @Preference
  @Option(name = "g", longName = "include-groups", optionalArg = true)
  private Boolean includeGroups = true;

  @Preference
  @Option(name = "m", longName = "include-meta", optionalArg = true)
  private Boolean includeMeta = true;

  @Preference
  @Option(name = "A", longName = "include-all", optionalArg = true)
  private Boolean includeAll;

  @Argument
  private String name;

  @Inject
  public HelpAction(final HelpPageManager helpPages) {
    this.helpPages = checkNotNull(helpPages);
  }

  @Inject
  public HelpAction installCompleters(final @Named("alias-name") Completer c1,
                                       final @Named("node-path") Completer c2,
                                       final @Named("meta-help-page-name") Completer c3)
  {
    setCompleters(new AggregateCompleter(c1, c2, c3), null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    // If there is no argument given, display all help pages in context
    if (name == null) {
      displayAvailable(context);
      return Result.SUCCESS;
    }

    // First try a direct match
    HelpPage page = helpPages.getPage(name);

    // if not direct match, then look for similar pages
    if (page == null) {
      Collection<HelpPage> pages = helpPages.getPages(
        query((Predicate<HelpPage>) it -> it.getName().contains(name) || it.getDescription().contains(name))
      );

      if (pages.size() == 1) {
        // if there is only one match, treat as a direct match
        page = pages.iterator().next();
      }
      else if (pages.size() > 1) {
        // else show matching pages
        io.out.println(getMessages().format("info.matching-pages"));
        HelpPageUtil.render(io.out, pages);
        return Result.SUCCESS;
      }
    }

    // if not page matched, complain
    if (page == null) {
      io.err.println(getMessages().format("error.help-not-found", name));
      return Result.FAILURE;
    }

    // render matched page; with pager or directly
    if (pager) {
      // TODO: consider adding a means to create these generically; and control if enabled via variable?
      Less less = new Less(io.getTerminal());
      try (ByteArrayOutputStream buff = new ByteArrayOutputStream()) {
        PrintWriter writer = new PrintWriter(buff);
        // help pages are expected to replace AnsiRender tokens, so wrap with stream
        page.render(new AnsiRenderWriter(writer));
        writer.close();
        less.run(new InputStreamSource(page.getName(), new ByteArrayInputStream(buff.toByteArray())));
      }
    }
    else {
      page.render(io.out);
    }

    return Result.SUCCESS;
  }

  private void displayAvailable(final CommandContext context) {
    Collection<HelpPage> pages = helpPages.getPages(query());
    IO io = context.getIo();
    io.out.println(getMessages().format("info.available-pages"));
    HelpPageUtil.render(io.out, pages);
  }

  // TODO: investigate "heap-pollution" warnings
  private Predicate<HelpPage> query(@Nullable final Predicate<HelpPage>... predicates) {
    PredicateBuilder<HelpPage> query = new PredicateBuilder<>();

    if (includeAll == null || !includeAll) {
      if (includeAliases != null && !includeAliases) {
        query.not(new TypePredicate<>(AliasHelpPage.class));
      }
      if (includeMeta != null && !includeMeta) {
        query.not(new TypePredicate<>(MetaHelpPage.class));
      }
      if (includeCommands != null && !includeCommands) {
        query.not(new TypePredicate<>(CommandHelpPage.class));
      }
      if (includeGroups != null && !includeGroups) {
        query.not(new TypePredicate<>(GroupHelpPage.class));
      }
    }

    if (predicates != null) {
      query.include(predicates);
    }

    return query.build();
  }
}