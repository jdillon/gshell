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
package com.planet57.gshell.command;

import org.jline.reader.Completer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides the user-action for a command.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface CommandAction
    extends Cloneable
{
  String getName();

  String getSimpleName();

  /**
   * @since 3.0
   */
  @Nullable
  String getDescription();

  /**
   * Execute the command action.
   *
   * @param context The execution context of the command.
   * @return The result of the command execution.
   * @throws Exception Command execution failed.
   */
  Object execute(@Nonnull CommandContext context) throws Exception;

  // FIXME: Find a better name for this; to avoid confusion with Throwable Notification
  class ExitNotification
  {
    public final int code;

    public ExitNotification(final int code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return "ExitNotification{" +
        "code=" + code +
        '}';
    }
  }

  /**
   * Allows commands to be informed of their names at runtime.
   */
  interface NameAware
  {
    void setName(String name);
  }

  /**
   * Commands which are modeled as prototypes.
   */
  interface Prototype
  {
    CommandAction create();
  }

  /**
   * Commands which support completion.
   */
  interface Completable
  {
    Completer getCompleter();
  }
}
