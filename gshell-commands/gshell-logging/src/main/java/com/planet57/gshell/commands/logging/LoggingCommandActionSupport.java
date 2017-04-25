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
package com.planet57.gshell.commands.logging;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.logging.LoggingSystem;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@code logging} commands.
 *
 * @since 3.0
 */
public abstract class LoggingCommandActionSupport
    extends CommandActionSupport
{
  @Nullable
  private LoggingSystem logging;

  @Inject
  public void setLogging(@Nullable final LoggingSystem logging) {
    this.logging = logging;
  }

  protected LoggingSystem getLogging() {
    checkState(logging != null, "Logging-system is not configured");

    return logging;
  }
}
