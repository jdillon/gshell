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
package com.planet57.gshell.testharness;

import java.util.Collection;
import java.util.Collections;

import com.planet57.gshell.logging.LevelComponent;
import com.planet57.gshell.logging.LoggingComponent;
import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.logging.LoggingSystem;

/**
 * Test {@link LoggingSystem}.
 *
 * @since 3.0
 */
public class TestLoggingSystem
    implements LoggingSystem
{
  @Override
  public LevelComponent getLevel(final String name) {
    return () -> name;
  }

  @Override
  public Collection<? extends LevelComponent> getLevels() {
    return Collections.emptySet();
  }

  @Override
  public LoggerComponent getLogger(final String name) {
    return new LoggerComponent()
    {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public LevelComponent getLevel() {
        return null;
      }

      @Override
      public void setLevel(LevelComponent level) {
        // ignore
      }

      @Override
      public void setLevel(String level) {
        // ignore
      }

      @Override
      public boolean isRoot() {
        return false;
      }
    };
  }

  @Override
  public Collection<String> getLoggerNames() {
    return Collections.emptySet();
  }

  @Override
  public Collection<? extends LoggingComponent> getComponents() {
    return Collections.emptySet();
  }
}
