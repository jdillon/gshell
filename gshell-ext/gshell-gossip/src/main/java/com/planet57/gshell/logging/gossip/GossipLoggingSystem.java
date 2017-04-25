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
package com.planet57.gshell.logging.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gossip.EffectiveProfile;
import com.planet57.gossip.Gossip;
import com.planet57.gossip.Level;
import com.planet57.gossip.listener.Listener;
import com.planet57.gshell.logging.LevelComponent;
import com.planet57.gshell.logging.LoggingComponent;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.logging.LoggingComponentSupport;
import com.planet57.gshell.logging.LoggingSystem;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.planet57.gossip.Gossip.LoggerImpl.ROOT_NAME;

/**
 * <a href="http://github.com/jdillon/gossip">Gossip</a> {@link LoggingSystem} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class GossipLoggingSystem
    implements LoggingSystem
{
  private final Gossip gossip;

  private final Map<String, LevelComponentImpl> levels;

  public GossipLoggingSystem() {
    // Make sure Gossip is actually configured, attach to the context
    Object tmp = LoggerFactory.getILoggerFactory();
    checkState(tmp instanceof Gossip, "SLF4J logger factory does not appear to be Gossip; found: %s", tmp.getClass().getName());
    gossip = Gossip.getInstance();

    // populate levels
    Map<String, LevelComponentImpl> levels = new LinkedHashMap<>();
    for (Level level : Level.values()) {
      levels.put(level.name().toUpperCase(Locale.US), new LevelComponentImpl(level));
    }
    this.levels = Collections.unmodifiableMap(levels);
  }

  //
  // LevelComponentImpl
  //

  private class LevelComponentImpl
      implements LevelComponent
  {
    private final Level target;

    private LevelComponentImpl(final Level level) {
      this.target = checkNotNull(level);
    }

    @Override
    public String getName() {
      return target.name();
    }

    public Level getTarget() {
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

  @Override
  public LevelComponent getLevel(final String name) {
    checkNotNull(name);

    LevelComponent level = levels.get(name.toUpperCase(Locale.US));
    checkArgument(level != null, "Invalid level name: %s", name);
    return level;
  }

  private LevelComponentImpl levelFor(final String name) {
    return (LevelComponentImpl) getLevel(name);
  }

  @Override
  public Collection<? extends LevelComponent> getLevels() {
    return levels.values();
  }

  //
  // LoggerComponentImpl
  //

  private class LoggerComponentImpl
      implements LoggerComponent
  {
    private final Gossip.LoggerImpl target;

    public LoggerComponentImpl(final Gossip.LoggerImpl logger) {
      this.target = checkNotNull(logger);
    }

    @Override
    public String getName() {
      return target.getName();
    }

    @Override
    public LevelComponent getLevel() {
      Level tmp = target.getLevel();
      if (tmp != null) {
        return levelFor(tmp.name());
      }
      return null;
    }

    @Override
    public void setLevel(final LevelComponent level) {
      target.setLevel(levelFor(level.getName()).getTarget());
    }

    @Override
    public void setLevel(final String level) {
      setLevel(levelFor(level));
    }

    @Override
    public boolean isRoot() {
      return getName().equals(ROOT_NAME);
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

  @Override
  public Collection<String> getLoggerNames() {
    return gossip.getLoggerNames();
  }

  @Override
  public LoggerComponent getLogger(final String name) {
    checkNotNull(name);
    return new LoggerComponentImpl(gossip.getLogger(name));
  }

  @Override
  public Collection<? extends LoggingComponent> getComponents() {
    List<LoggingComponent> components = new ArrayList<>();

    components.add(new LoggingComponentSupport(gossip));
    EffectiveProfile effectiveProfile = gossip.getEffectiveProfile();
    components.add(new LoggingComponentSupport(effectiveProfile));
    for (Listener listener : effectiveProfile.getListeners()) {
      components.add(new LoggingComponentSupport(listener));
    }

    return components;
  }
}
