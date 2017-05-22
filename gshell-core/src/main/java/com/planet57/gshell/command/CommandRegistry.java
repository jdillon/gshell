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

import org.sonatype.goodies.lifecycle.Lifecycle;

import java.util.Collection;

/**
 * Registry for commands.
 *
 * @since 2.5
 */
public interface CommandRegistry
  extends Lifecycle
{
  void registerCommand(String name, CommandAction command) throws DuplicateCommandException;

  void removeCommand(String name) throws NoSuchCommandException;

  CommandAction getCommand(String name) throws NoSuchCommandException;

  boolean containsCommand(String name);

  Collection<CommandAction> getCommands();

  /**
   * Thrown to indicate a duplicate command registration attempt has failed.
   *
   * @since 3.0
   */
  class DuplicateCommandException
      extends Exception
  {
    private static final long serialVersionUID = 1;

    public DuplicateCommandException(final String msg) {
      super(msg);
    }
  }

  /**
   * Thrown to indicate that a requested named-command was not found.
   *
   * @since 3.0
   */
  class NoSuchCommandException
      extends Exception
  {
    private static final long serialVersionUID = 1;

    public NoSuchCommandException(final String msg) {
      super(msg);
    }
  }
}
