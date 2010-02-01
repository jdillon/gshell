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

import com.google.inject.Inject;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.registry.NoSuchAliasException;
import org.sonatype.gshell.registry.NoSuchCommandException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link HelpPageManager} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpPageManagerImpl
    implements HelpPageManager
{
    private final AliasRegistry aliasRegistry;

    private final CommandRegistry commandRegistry;

    private final HelpContentLoader helpLoader;

    @Inject
    public HelpPageManagerImpl(final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry, final HelpContentLoader helpLoader) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
        assert helpLoader != null;
        this.helpLoader = helpLoader;
    }

    public HelpPage getPage(final String path) {
        assert path != null;

        if (aliasRegistry.containsAlias(path)) {
            try {
                return new AliasHelpPage(path, aliasRegistry.getAlias(path));
            }
            catch (NoSuchAliasException e) {
                throw new Error(e);
            }
        }

        if (commandRegistry.containsCommand(path)) {
            try {
                return new CommandHelpPage(commandRegistry.getCommand(path), helpLoader);
            }
            catch (NoSuchCommandException e) {
                throw new Error(e);
            }
        }

        // else check for meta pages

        return null;
    }

    public Collection<HelpPage> getPages(final String path) {
        // path may be null
        
        // HACK: For now just return commands, since that is how it used to work.

        List<HelpPage> pages = new ArrayList<HelpPage>();

        for (String name : commandRegistry.getCommandNames()) {
            try {
                pages.add(new CommandHelpPage(commandRegistry.getCommand(name), helpLoader));
            }
            catch (NoSuchCommandException e) {
                throw new Error(e);
            }
        }

        return pages;
    }
}