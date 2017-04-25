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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Singleton;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.collect.ImmutableList;
import com.planet57.gshell.logging.LevelComponent;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.logging.LoggingComponent;
import com.planet57.gshell.logging.LoggingComponentSupport;
import com.planet57.gshell.logging.LoggingSystem;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * <a href="http://logback.qos.ch/">LOGBack</a> {@link LoggingSystem} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.6.4
 */
@Named
@Singleton
public class LogbackLoggingSystem
    implements LoggingSystem
{
  private static final List<Level> ALL_LEVELS = ImmutableList.of(
    Level.ALL,
    Level.TRACE,
    Level.DEBUG,
    Level.INFO,
    Level.WARN,
    Level.ERROR,
    Level.OFF
  );

  private final LoggerContext loggerContext;

  private final Map<String, LevelComponentImpl> levels;

  public LogbackLoggingSystem() {
    // Make sure Logback is actually configured, attach to the context
    Object tmp = LoggerFactory.getILoggerFactory();
    checkState(tmp instanceof LoggerContext, "SLF4J logger factory does not appear to be LOGBack; found: %s", tmp.getClass().getName());
    this.loggerContext = (LoggerContext) tmp;

    // generate logger level mapping
    this.levels = ALL_LEVELS.stream().collect(Collectors.toMap(level -> level.toString().toUpperCase(Locale.US), LevelComponentImpl::new));
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
      return target.toString();
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
    private final Logger target;

    public LoggerComponentImpl(final Logger logger) {
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
        return levelFor(tmp.toString());
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

  @Override
  public Collection<String> getLoggerNames() {
    return loggerContext.getLoggerList().stream().map(Logger::getName).collect(Collectors.toList());
  }

  @Override
  public LoggerComponent getLogger(final String name) {
    checkNotNull(name);
    return new LoggerComponentImpl(loggerContext.getLogger(name));
  }

  @Override
  public Collection<? extends LoggingComponent> getComponents() {
    List<LoggingComponent> components = new ArrayList<>();

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    components.add(new LoggingComponentSupport(context));
    context.getCopyOfListenerList().forEach(listener -> components.add(new LoggingComponentSupport(listener)));
    context.getTurboFilterList().forEach(filter -> components.add(new LoggingComponentSupport(filter)));

    return components;
  }
}
