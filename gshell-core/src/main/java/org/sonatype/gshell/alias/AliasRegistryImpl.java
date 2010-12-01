/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.alias;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.event.EventManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link AliasRegistry} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class AliasRegistryImpl
    implements AliasRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, String> aliases = new LinkedHashMap<String, String>();

    private final EventManager events;

    @Inject
    public AliasRegistryImpl(final EventManager events) {
        assert events != null;
        this.events = events;
    }

    public void registerAlias(final String name, final String alias) {
        assert name != null;
        assert alias != null;

        log.debug("Registering alias: {} -> {}", name, alias);

        if (log.isDebugEnabled()) {
            if (containsAlias(name)) {
                log.debug("Replacing alias: {}", name);
            }
        }

        aliases.put(name, alias);

        events.publish(new AliasRegisteredEvent(name, alias));
    }

    public void removeAlias(final String name) throws NoSuchAliasException {
        assert name != null;

        log.debug("Removing alias: {}", name);

        if (!containsAlias(name)) {
            throw new NoSuchAliasException(name);
        }

        aliases.remove(name);

        events.publish(new AliasRemovedEvent(name));
    }

    public String getAlias(final String name) throws NoSuchAliasException {
        assert name != null;

        if (!containsAlias(name)) {
            throw new NoSuchAliasException(name);
        }

        return aliases.get(name);
    }

    public boolean containsAlias(final String name) {
        assert name != null;

        return aliases.containsKey(name);
    }

    public Collection<String> getAliasNames() {
        return Collections.unmodifiableSet(aliases.keySet());
    }

    public Map<String,String> getAliases() {
        return Collections.unmodifiableMap(aliases);
    }
}