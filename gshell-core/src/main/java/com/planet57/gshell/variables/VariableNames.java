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
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface VariableNames
{
    String SHELL_HOME = "shell.home";

    String SHELL_PROGRAM = "shell.program";

    String SHELL_VERSION = "shell.version";

    String SHELL_USER_DIR = "shell.user.dir";

    String SHELL_USER_HOME = "shell.user.home";

    String SHELL_PROMPT = "shell.prompt";

    String SHELL_HISTORY = "shell.history";

    String SHELL_ERRORS = "shell.errors";

    String SHELL_LOGGING = "shell.logging";

    /**
     * @since 2.5
     */
    String SHELL_GROUP = "shell.group";

    /**
     * @since 2.5
     */
    String SHELL_GROUP_PATH = "shell.group.path";

    String LAST_RESULT = "_";
}