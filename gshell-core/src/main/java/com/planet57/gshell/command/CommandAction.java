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

import com.planet57.gshell.util.i18n.MessageSource;
import jline.console.completer.Completer;

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

  @Nullable
  MessageSource getMessages();

  Completer[] getCompleters();

  /**
   * Execute the command action.
   *
   * @param context The execution context of the command.
   * @return The result of the command execution.
   * @throws Exception Command execution failed.
   */
  Object execute(CommandContext context) throws Exception;

  /**
   * Enumeration for the basic return types of a command execution.
   */
  enum Result
  {
    /**
     * The command execution was successful.
     */
    SUCCESS, // 0

    /**
     * The command execution failed.
     */
    FAILURE // 1
  }

  /**
   * Marker for commands that need to be aware of their names.
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
}
