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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Key;
import com.planet57.gshell.alias.AliasRegistry;
import com.planet57.gshell.alias.NoSuchAliasException;
import com.planet57.gshell.command.resolver.CommandResolver;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.event.EventManager;
import com.planet57.gshell.guice.BeanContainer;
import org.sonatype.goodies.common.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link HelpPageManager}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class HelpPageManagerImpl
  extends ComponentSupport
  implements HelpPageManager
{
  private final BeanContainer container;

  private final EventManager events;

  private final AliasRegistry aliases;

  private final CommandResolver resolver;

  private final HelpContentLoader loader;

  private final Map<String, MetaHelpPage> metaPages = new LinkedHashMap<>();

  @Inject
  public HelpPageManagerImpl(final BeanContainer container,
                             final EventManager events,
                             final AliasRegistry aliases,
                             final CommandResolver resolver,
                             final HelpContentLoader loader)
  {
    this.container = checkNotNull(container);
    this.events = checkNotNull(events);
    this.aliases = checkNotNull(aliases);
    this.resolver = checkNotNull(resolver);
    this.loader = checkNotNull(loader);

    // FIXME: move to lifecycle; and allow disabling
    discoverMetaPages();
  }

  private void discoverMetaPages() {
    log.trace("Discovering meta-pages");

    container.locate(Key.get(MetaHelpPage.class)).forEach(entry -> addMetaPage(entry.getValue()));
  }

  @Override
  public HelpPage getPage(final String name) {
    assert name != null;

    if (aliases.containsAlias(name)) {
      try {
        return new AliasHelpPage(name, aliases.getAlias(name));
      }
      catch (NoSuchAliasException e) {
        throw new Error(e);
      }
    }

    Node node = resolver.resolve(name);
    if (node != null) {
      return HelpPageUtil.pageFor(node, loader);
    }

    if (metaPages.containsKey(name)) {
      return metaPages.get(name);
    }

    return null;
  }

  @Override
  public Collection<HelpPage> getPages(final Predicate<HelpPage> query) {
    checkNotNull(query);
    return getPages().stream().filter(query).collect(Collectors.toList());
  }

  @Override
  public Collection<HelpPage> getPages() {
    Map<String, HelpPage> pages = new TreeMap<>();

    // Add aliases
    aliases.getAliases().forEach((key, value) -> pages.put(key, new AliasHelpPage(key, value)));

    // Add commands
    for (Node parent : resolver.searchPath()) {
      if (parent.isGroup()) {
        for (Node child : parent.children()) {
          if (!pages.containsKey(child.getName())) {
            pages.put(child.getName(), HelpPageUtil.pageFor(child, loader));
          }
        }
      }
      else if (!pages.containsKey(parent.getName())) {
        pages.put(parent.getName(), HelpPageUtil.pageFor(parent, loader));
      }
    }

    // Add meta-pages
    getMetaPages().stream()
      .filter(page -> !pages.containsKey(page.getName()))
      .forEach(page -> pages.put(page.getName(), page));

    return pages.values();
  }

  @Override
  public void addMetaPage(final MetaHelpPage page) {
    checkNotNull(page);

    log.debug("Adding meta-page: {}", page);
    metaPages.put(page.getName(), page);
    events.publish(new MetaHelpPageAddedEvent(page));
  }

  @Override
  public Collection<MetaHelpPage> getMetaPages() {
    return metaPages.values();
  }
}
