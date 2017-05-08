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

import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.variables.Variables;
import org.apache.felix.gogo.runtime.CommandSessionImpl;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Provides commands with the context of it's execution.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface CommandContext
{
  /**
   * The invoking shell instance.
   *
   * @return The invoking shell instance; never null.
   */
  @Nonnull
  Shell getShell();

  /**
   * @since 3.0
   */
  @Nonnull
  CommandSessionImpl getSession();

  /**
   * Provides access to the arguments to the command.
   *
   * @return The command arguments; never null.
   */
  @Nonnull
  List<?> getArguments();

  /**
   * The input/output for the command.
   *
   * @return Command input/output; never null.
   */
  @Nonnull
  IO getIo();

  /**
   * The variables for the command.
   *
   * @return Command variables; never null.
   */
  @Nonnull
  Variables getVariables();
}
