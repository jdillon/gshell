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
package org.sonatype.gshell.commands.standard;

import javax.inject.Inject;
import javax.inject.Named;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.Completer;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.help.AliasHelpPage;
import org.sonatype.gshell.help.CommandHelpPage;
import org.sonatype.gshell.help.GroupHelpPage;
import org.sonatype.gshell.help.HelpPage;
import org.sonatype.gshell.help.HelpPageManager;
import org.sonatype.gshell.help.HelpPageUtil;
import org.sonatype.gshell.help.MetaHelpPage;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.util.filter.AggregateFilter;
import org.sonatype.gshell.util.filter.AndFilter;
import org.sonatype.gshell.util.filter.Filter;
import org.sonatype.gshell.util.filter.TypeFilter;
import org.sonatype.gshell.util.pref.Preference;
import org.sonatype.gshell.util.pref.Preferences;

import java.util.Collection;
import java.util.Collections;

/**
 * Display help pages.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="help")
@Preferences(path = "commands/help")
public class HelpCommand
    extends CommandActionSupport
{
    private final HelpPageManager helpPages;

    // TODO: maybe use an enum here to say; --include groups,commands,aliases (exclude meta) etc...
    
    @Preference
    @Option(name="c", longName="include-commands", optionalArg=true)
    private Boolean includeCommands = true;

    @Preference
    @Option(name="a", longName="include-aliases", optionalArg=true)
    private Boolean includeAliases = true;

    @Preference
    @Option(name="g", longName="include-groups", optionalArg=true)
    private Boolean includeGroups = true;

    @Preference
    @Option(name="m", longName="include-meta", optionalArg=true)
    private Boolean includeMeta = true;

    @Preference
    @Option(name="A", longName="include-all", optionalArg=true)
    private Boolean includeAll;
    
    @Argument
    private String name;

    @Inject
    public HelpCommand(final HelpPageManager helpPages) {
        assert helpPages != null;
        this.helpPages = helpPages;
    }

    @Inject
    public HelpCommand installCompleters(final @Named("alias-name") Completer c1,
                                         final @Named("node-path") Completer c2,
                                         final @Named("meta-help-page-name") Completer c3)
    {
        setCompleters(new AggregateCompleter(c1, c2, c3), null);
        return this;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
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
            Collection<HelpPage> pages = helpPages.getPages(filter(new Filter<HelpPage>()
            {
                public boolean accept(final HelpPage page) {
                    assert page != null;
                    return page.getName().contains(name) || page.getDescription().contains(name);
                }
            }));

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

        // else render the matched page
        page.render(io.out);
        return Result.SUCCESS;
    }

    private void displayAvailable(final CommandContext context) {
        assert context != null;

        Collection<HelpPage> pages = helpPages.getPages(filter());
        IO io = context.getIo();
        io.out.println(getMessages().format("info.available-pages"));
        HelpPageUtil.render(io.out, pages);
    }

    private AggregateFilter<HelpPage> filter(final Filter<HelpPage>... filters) {
        // filters may be null
        
        AndFilter<HelpPage> filter = new AndFilter<HelpPage>();
        if (includeAll == null || !includeAll) {
            if (includeAliases != null && !includeAliases) {
                filter.not(new TypeFilter<HelpPage>(AliasHelpPage.class));
            }
            if (includeMeta != null && !includeMeta) {
                filter.not(new TypeFilter<HelpPage>(MetaHelpPage.class));
            }
            if (includeCommands != null && !includeCommands) {
                filter.not(new TypeFilter<HelpPage>(CommandHelpPage.class));
            }
            if (includeGroups != null && !includeGroups) {
                filter.not(new TypeFilter<HelpPage>(GroupHelpPage.class));
            }
        }

        if (filters != null) {
            Collections.addAll(filter.getFilters(), filters);
        }

        return filter;
    }
}