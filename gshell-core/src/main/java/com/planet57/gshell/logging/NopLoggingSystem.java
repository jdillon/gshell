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
package com.planet57.gshell.logging;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;

/**
 * Provides generic access to the underlying logging system.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named
@Singleton
public class NopLoggingSystem
    implements LoggingSystem
{
  @Override
  public Level getLevel(String name) {
    return new Level()
    {
      public String getName() {
        return null;
      }
    };
  }

  @Override
  public Collection<? extends Level> getLevels() {
    return Collections.emptyList();
  }

  @Override
  public Logger getLogger(String name) {
    return new Logger()
    {
      @Override
      public String getName() {
        return null;
      }

      @Override
      public Level getLevel() {
        return null;
      }

      @Override
      public void setLevel(Level level) {
        // empty
      }

      @Override
      public void setLevel(String level) {
        // empty
      }

      @Override
      public boolean isRoot() {
        return false;
      }
    };
  }

  @Override
  public Collection<String> getLoggerNames() {
    return Collections.emptyList();
  }

  @Override
  public Collection<? extends Component> getComponents() {
    return Collections.emptyList();
  }
}
