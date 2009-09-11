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

package org.apache.maven.shell.command;

import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.Shell;
import org.apache.maven.shell.Variables;

/**
 * Provides commands with the context of it's execution.
 *
 * @version $Rev$ $Date$
 */
public interface CommandContext
{
    Shell getShell();
    
    /**
     * Provides access to the arguments to the command.
     *
     * @return The command arguments; never null.
     */
    String[] getArguments();

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
}