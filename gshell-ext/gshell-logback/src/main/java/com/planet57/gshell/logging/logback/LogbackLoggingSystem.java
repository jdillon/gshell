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
package com.planet57.gshell.logging.logback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import ch.qos.logback.classic.LoggerContext;
import com.planet57.gshell.logging.Component;
import com.planet57.gshell.logging.Level;
import com.planet57.gshell.logging.Logger;
import com.planet57.gshell.logging.LoggingSystem;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * <a href="http://logback.qos.ch/">LOGBack</a> {@link LoggingSystem} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.6.4
 */
@Singleton
public class LogbackLoggingSystem
    implements LoggingSystem
{
  private final LoggerContext loggerContext;

  private final Map<String, LevelImpl> levels;

  private final Set<Component> components;

  public LogbackLoggingSystem() {
    // Make sure Logback is actually configured, attach to the context
    Object tmp = LoggerFactory.getILoggerFactory();
    if (!(tmp instanceof LoggerContext)) {
      throw new RuntimeException(
          "SLF4J logger factory does not appear to be LOGBack; found: " + tmp.getClass().getName());
    }
    this.loggerContext = (LoggerContext) tmp;

    // populate levels
    Map<String, LevelImpl> levels = new LinkedHashMap<String, LevelImpl>();

    ch.qos.logback.classic.Level[] source = {
        ch.qos.logback.classic.Level.ALL,
        ch.qos.logback.classic.Level.TRACE,
        ch.qos.logback.classic.Level.DEBUG,
        ch.qos.logback.classic.Level.INFO,
        ch.qos.logback.classic.Level.WARN,
        ch.qos.logback.classic.Level.ERROR,
        ch.qos.logback.classic.Level.OFF,
        };

    for (ch.qos.logback.classic.Level level : source) {
      levels.put(level.toString(), new LevelImpl(level));
    }

    this.levels = Collections.unmodifiableMap(levels);

    // setup components map
    components = new LinkedHashSet<Component>();
  }

  //
  // LevelImpl
  //

  private class LevelImpl
      implements Level
  {
    private final ch.qos.logback.classic.Level target;

    private LevelImpl(final ch.qos.logback.classic.Level level) {
      this.target = checkNotNull(level);
    }

    @Override
    public String getName() {
      return target.toString();
    }

    public ch.qos.logback.classic.Level getTarget() {
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
  public Level getLevel(final String name) {
    checkNotNull(name);

    Level level = levels.get(name.toUpperCase());
    if (level == null) {
      throw new RuntimeException("Invalid level name: " + name);
    }
    return level;
  }

  private LevelImpl levelFor(final String name) {
    return (LevelImpl) getLevel(name);
  }

  @Override
  public Collection<? extends Level> getLevels() {
    return levels.values();
  }

  //
  // LoggerImpl
  //

  private class LoggerImpl
      implements Logger
  {
    private final ch.qos.logback.classic.Logger target;

    public LoggerImpl(final ch.qos.logback.classic.Logger logger) {
      this.target = checkNotNull(logger);
    }

    @Override
    public String getName() {
      return target.getName();
    }

    @Override
    public Level getLevel() {
      ch.qos.logback.classic.Level tmp = target.getLevel();
      if (tmp != null) {
        return levelFor(tmp.toString());
      }
      return null;
    }

    @Override
    public void setLevel(final Level level) {
      target.setLevel(levelFor(level.getName()).getTarget());
    }

    @Override
    public void setLevel(final String level) {
      setLevel(levelFor(level));
    }

    @Override
    public boolean isRoot() {
      return getName().equals(ROOT_LOGGER_NAME);
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

  // NOTE: This doesn't seem to be returning all loggers that have been created, just those which have been configured/bound to a level :-(

  @Override
  public Collection<String> getLoggerNames() {
    Collection<ch.qos.logback.classic.Logger> loggers = loggerContext.getLoggerList();
    List<String> names = new ArrayList<String>(loggers.size());
    for (ch.qos.logback.classic.Logger logger : loggers) {
      names.add(logger.getName());
    }
    return names;
  }

  @Override
  public Logger getLogger(final String name) {
    checkNotNull(name);
    return new LoggerImpl(loggerContext.getLogger(name));
  }

  @Override
  public Collection<? extends Component> getComponents() {
    // TODO: Expose appenders and whatever
    return Collections.emptySet();
  }
}
