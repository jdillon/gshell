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

package org.sonatype.gshell.logging.gossip;

import com.google.inject.Singleton;
import org.sonatype.gossip.Gossip;
import org.sonatype.gossip.listener.Listener;
import org.sonatype.gshell.logging.Component;
import org.sonatype.gshell.logging.Level;
import org.sonatype.gshell.logging.Logger;
import org.sonatype.gshell.logging.LoggingSystem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <a href="http://github.com/jdillon/gossip">Gossip</a> {@link LoggingSystem} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class GossipLoggingSystem
    implements LoggingSystem
{
    private final Gossip gossip;

    private final Map<String,LevelImpl> levels;

    private final Set<Component> components;

    public GossipLoggingSystem() {
        gossip = Gossip.getInstance();

        // populate levels
        Map<String,LevelImpl> levels = new LinkedHashMap<String,LevelImpl>();
        for (Gossip.Level level : Gossip.Level.values()) {
            levels.put(level.name().toUpperCase(), new LevelImpl(level));
        }
        this.levels = Collections.unmodifiableMap(levels);

        // setup components map
        components = new LinkedHashSet<Component>();
        // leave the rest to lazy init for now
    }

    //
    // LevelImpl
    //

    private class LevelImpl
        implements Level
    {
        private final Gossip.Level target;

        private LevelImpl(final Gossip.Level level) {
            assert level != null;
            this.target = level;
        }

        public String getName() {
            return target.name();
        }

        public Gossip.Level getTarget() {
            return target;
        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public Level getLevel(final String name) {
        assert name != null;
        Level level = levels.get(name.toUpperCase());
        if (level == null) {
            throw new RuntimeException("Invalid level name: " + name);
        }
        return level;
    }

    private LevelImpl levelFor(final String name) {
        return (LevelImpl)getLevel(name);
    }

    public Collection<? extends Level> getLevels() {
        return levels.values();
    }

    //
    // LoggerImpl
    //

    private class LoggerImpl
        implements Logger
    {
        private final Gossip.Logger target;

        public LoggerImpl(final Gossip.Logger logger) {
            assert logger != null;
            this.target = logger;
        }

        public String getName() {
            return target.getName();
        }

        public Level getLevel() {
            Gossip.Level tmp = target.getLevel();
            if (tmp != null) {
                return levelFor(tmp.name());
            }
            return null;
        }

        public void setLevel(final Level level) {
            target.setLevel(levelFor(level.getName()).getTarget());
        }

        public void setLevel(final String level) {
            setLevel(levelFor(level));
        }

        public Logger parent() {
            return target.getParent() != null ? new LoggerImpl(target.getParent()) : null;
        }

        public boolean isRoot() {
            return getName().equals(Gossip.Logger.ROOT_NAME);
        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }
        
        @Override
        public String toString() {
            return getName();
        }
    }

    public Collection<String> getLoggerNames() {
        return gossip.getLoggerNames();
    }

    public Logger getLogger(final String name) {
        assert name != null;
        return new LoggerImpl(gossip.getLogger(name));
    }

    public Collection<Component> getComponents() {
        synchronized (components) {
            if (components.isEmpty()) {
                components.add(new EffectiveProfileComponent(gossip.getEffectiveProfile()));

                for (Listener listener : gossip.getEffectiveProfile().getListeners()) {
                    components.add(new ListenerComponent(listener));
                }
                
                // TODO: Add the rest of the components
            }
        }
        return Collections.unmodifiableSet(components);
    }
}