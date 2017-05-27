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
package com.planet57.gshell.plexus.internal;

import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.codehaus.plexus.logging.Logger.LEVEL_DEBUG;

/**
 * Adapts the Plexus logging system to <a href="http://slf4j.org">SLF4J</a>.
 *
 * @since 3.0
 */
public class Slf4jLoggerManager
    extends AbstractLoggerManager
{
  protected org.codehaus.plexus.logging.Logger createLogger(final String key) {
    return new LoggerImpl(key);
  }

  @Override
  public org.codehaus.plexus.logging.Logger getLoggerForComponent(final String role, final String roleHint) {
    return createLogger(toMapKey(role, roleHint));
  }

  protected String toMapKey(final String role, final String roleHint) {
    if (roleHint == null) {
      return role;
    }
    else {
      return role + ":" + roleHint;
    }
  }

  @Override
  public void returnComponentLogger(final String role, final String roleHint) {
    // Ignore
  }

  @Override
  public void setThreshold(final int threshold) {
    // empty
  }

  @Override
  public int getThreshold() {
    return LEVEL_DEBUG;
  }

  @Override
  public void setThresholds(final int threshold) {
    // empty
  }

  public void setThreshold(final String role, final String roleHint, final int threshold) {
    // empty
  }

  public int getThreshold(final String role, final String roleHint) {
    return LEVEL_DEBUG;
  }

  @Override
  public int getActiveLoggerCount() {
    return 0;
  }

  /**
   * Adapts the Plexus {@link org.codehaus.plexus.logging.Logger} interface to SLF4J
   */
  public static class LoggerImpl
      implements org.codehaus.plexus.logging.Logger
  {
    private final Logger log;

    public LoggerImpl(final String name) {
      checkNotNull(name);
      this.log = LoggerFactory.getLogger(name);
    }

    @Override
    public void debug(String message) {
      log.debug(message);
    }

    @Override
    public void debug(String message, Throwable throwable) {
      log.debug(message, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
      return log.isDebugEnabled();
    }

    @Override
    public void info(String message) {
      log.info(message);
    }

    @Override
    public void info(String message, Throwable throwable) {
      log.info(message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
      return log.isInfoEnabled();
    }

    @Override
    public void warn(String message) {
      log.warn(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
      log.warn(message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
      return log.isWarnEnabled();
    }

    @Override
    public void error(String message) {
      log.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
      log.error(message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
      return log.isErrorEnabled();
    }

    @Override
    public void fatalError(String message) {
      error(message);
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
      error(message, throwable);
    }

    @Override
    public boolean isFatalErrorEnabled() {
      return isErrorEnabled();
    }

    @Override
    public org.codehaus.plexus.logging.Logger getChildLogger(String name) {
      return new LoggerImpl(log.getName() + "." + name);
    }

    @Override
    public int getThreshold() {
      return LEVEL_DEBUG;
    }

    @Override
    public void setThreshold(int threshold) {
      // nothing
    }

    @Override
    public String getName() {
      return log.getName();
    }
  }
}
