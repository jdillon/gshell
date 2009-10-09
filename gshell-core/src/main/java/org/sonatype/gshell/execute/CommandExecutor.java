/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sonatype.gshell.execute;

import org.sonatype.gshell.Shell;

/**
 * Provides the ability to execute commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public interface CommandExecutor
{
    /**
     * Execute a command-line, parsing out valid recognized syntax.
     *
     * @param shell     The executing shell.
     * @param line      Raw command-line to parse and execute.
     * @return          Command execution result.
     *
     * @throws Exception    Command-line execution failed.
     */
    Object execute(Shell shell, String line) throws Exception;

    /**
     * Execute command name/path with the given arguments.
     *
     * @param shell     The executing shell.
     * @param command   Name of the command/path to execute.
     * @param args      Command arguments.
     * @return          Command execution result.
     *
     * @throws Exception    Command-line execution failed.
     */
    Object execute(Shell shell, String command, Object[] args) throws Exception;

    /**
     * Execute a pre-processed command-line.
     *
     * @param shell     The executing shell.
     * @param args      Command arguments, first argument is expected to be the command/path to execute.
     * @return          Command execution result.
     *
     * @throws Exception    Command-line execution failed.
     */
    Object execute(Shell shell, Object... args) throws Exception;

}