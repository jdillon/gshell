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
package org.sonatype.gshell.shell;

import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.variables.Variables;

/**
 * Provides access to execute commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface Shell
{
    Branding getBranding();

    IO getIo();

    Variables getVariables();

    History getHistory();

    boolean isOpened();

    void close();

    Object execute(CharSequence line) throws Exception;

    Object execute(CharSequence command, Object[] args) throws Exception;

    Object execute(Object... args) throws Exception;

    /**
     * Check if the shell can be run interactively.
     *
     * @return True if the shell is interactive.
     */
    boolean isInteractive();

    /**
     * Run the shell interactively.
     *
     * @param args The initial commands to execute interactively.
     * @throws Exception Failed to execute commands.
     * @throws UnsupportedOperationException The shell does not support interactive execution.
     */
    void run(Object... args) throws Exception;
}
