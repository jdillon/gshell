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
package com.planet57.gshell.variables;

/**
 * Common shell variable names.
 *
 * @since 2.0
 */
public class VariableNames
{
  private VariableNames() {
    // empty
  }

  public static final String SHELL_HOME = "shell.home";

  public static final String SHELL_PROGRAM = "shell.program";

  public static final String SHELL_VERSION = "shell.version";

  public static final String SHELL_USER_DIR = "shell.user.dir";

  public static final String SHELL_USER_HOME = "shell.user.home";

  public static final String SHELL_PROMPT = "shell.prompt";

  /**
   * @since 3.0
   */
  public static final String SHELL_RPROMPT = "shell.rprompt";

  public static final String SHELL_ERRORS = "shell.errors";

  /**
   * @since 2.5
   */
  public static final String SHELL_GROUP = "shell.group";

  /**
   * @since 2.5
   */
  public static final String SHELL_GROUP_PATH = "shell.group.path";

  public static final String LAST_RESULT = "shell.result";
}
