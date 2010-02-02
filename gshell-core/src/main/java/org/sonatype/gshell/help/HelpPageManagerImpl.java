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
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.alias.NoSuchAliasException;
import org.sonatype.gshell.command.CommandRegistry;
import org.sonatype.gshell.command.descriptor.DiscoveredCommandSetDescriptorEvent;
import org.sonatype.gshell.command.NoSuchCommandException;
import org.sonatype.gshell.command.descriptor.HelpPageDescriptor;
import org.sonatype.gshell.event.EventListener;
import org.sonatype.gshell.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
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

    private final EventManager events;

    private final AliasRegistry aliasRegistry;

    private final CommandRegistry commandRegistry;

    private final HelpContentLoader helpLoader;

    private final Map<String,MetaHelpPage> metaPages = new HashMap<String,MetaHelpPage>();

    @Inject
    public HelpPageManagerImpl(final EventManager events, final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry, final HelpContentLoader helpLoader) {
        assert events != null;
        this.events = events;
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
        assert helpLoader != null;
        this.helpLoader = helpLoader;

        events.addListener(new EventListener()
        {
            public void onEvent(final EventObject event) throws Exception {
                assert event != null;
                if (event instanceof DiscoveredCommandSetDescriptorEvent) {
                    DiscoveredCommandSetDescriptorEvent target = (DiscoveredCommandSetDescriptorEvent)event;
                    for (HelpPageDescriptor page : target.getDescriptor().getHelpPages()) {
                        addMetaPage(page);
                    }
                }
            }
        });
    }

    public HelpPage getPage(final String name) {
        assert name != null;

        if (aliasRegistry.containsAlias(name)) {
            try {
                return new AliasHelpPage(name, aliasRegistry.getAlias(name));
            }
            catch (NoSuchAliasException e) {
                throw new Error(e);
            }
        }

        if (commandRegistry.containsCommand(name)) {
            try {
                return new CommandHelpPage(commandRegistry.getCommand(name), helpLoader);
            }
            catch (NoSuchCommandException e) {
                throw new Error(e);
            }
        }

        if (metaPages.containsKey(name)) {
            return metaPages.get(name);
        }
        
        return null;
    }

    public Collection<HelpPage> getPages(final HelpPageFilter filter) {
        assert filter != null;

        List<HelpPage> pages = new ArrayList<HelpPage>();

        for (String name : commandRegistry.getCommandNames()) {
            try {
                HelpPage page = new CommandHelpPage(commandRegistry.getCommand(name), helpLoader);
                if (filter.accept(page)) {
                    pages.add(page);
                }
            }
            catch (NoSuchCommandException e) {
                throw new Error(e);
            }
        }

        return pages;
    }

    public Collection<HelpPage> getPages() {
        return getPages(new HelpPageFilter() {
            public boolean accept(final HelpPage page)
            {
                return true;
            }
        });
    }

    public void addMetaPage(final HelpPageDescriptor desc) {
        assert desc != null;

        log.debug("Adding meta-page: {} -> {}", desc.getName(), desc.getResource());
        metaPages.put(desc.getName(), new MetaHelpPage(desc, helpLoader));
        events.publish(new MetaHelpPageAddedEvent(desc));
    }

    public Collection<MetaHelpPage> getMetaPages() {
        return metaPages.values();
    }
}