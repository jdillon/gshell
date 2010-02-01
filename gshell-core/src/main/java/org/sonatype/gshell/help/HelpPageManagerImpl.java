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
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.descriptor.HelpPageDescriptor;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.registry.NoSuchAliasException;
import org.sonatype.gshell.registry.NoSuchCommandException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link HelpPageManager} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class HelpPageManagerImpl
    implements HelpPageManager
{
    private static final Logger log = LoggerFactory.getLogger(HelpPageManagerImpl.class);

    private final EventManager eventManager;

    private final AliasRegistry aliasRegistry;

    private final CommandRegistry commandRegistry;

    private final HelpContentLoader helpLoader;

    private final Map<String,MetaHelpPage> metaPages = new HashMap<String,MetaHelpPage>();

    @Inject
    public HelpPageManagerImpl(final EventManager eventManager, final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry, final HelpContentLoader helpLoader) {
        assert eventManager != null;
        this.eventManager = eventManager;
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

        if (metaPages.containsKey(path)) {
            return metaPages.get(path);
        }
        
        return null;
    }

    public Collection<HelpPage> getPages() {
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

    public void addMetaPage(final HelpPageDescriptor desc) {
        assert desc != null;

        log.debug("Adding meta-page: {} -> {}", desc.getName(), desc.getResource());
        metaPages.put(desc.getName(), new MetaHelpPage(desc, helpLoader));

        eventManager.publish(new MetaHelpPageAddedEvent(desc));
    }

    public Collection<MetaHelpPage> getMetaPages() {
        return metaPages.values();
    }
}