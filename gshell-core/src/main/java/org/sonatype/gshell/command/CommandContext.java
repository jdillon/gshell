/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.command;

import org.sonatype.gshell.io.PromptReader;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.vars.Variables;

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
    Shell getShell();

    /**
     * Provides access to the arguments to the command.
     *
     * @return The command arguments; never null.
     */
    Object[] getArguments();

    /**
     * The Input/Output context for the command.
     *
     * @return Command Input/Output context; never null.
     */
    IO getIo();

    /**
     * The variables for the command.
     *
     * @return Command variables; never null.
     */
    Variables getVariables();

    // TODO: Consider adding generic state set/get muck here to allow commands to pass state internally
}