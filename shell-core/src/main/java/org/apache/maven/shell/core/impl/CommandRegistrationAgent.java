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

package org.apache.maven.shell.core.impl;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.shell.registry.CommandRegistry;
import org.apache.maven.shell.registry.DuplicateCommandException;
import org.apache.maven.shell.command.CommandException;

/**
 * Registers commands.
 *
 * @version $Rev$ $Date$
 */
@Component(role=CommandRegistrationAgent.class)
public class CommandRegistrationAgent
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private CommandRegistry commandRegistry;

    public void registerCommands() throws CommandException {
        log.debug("Registering commands");
        
        commandRegistry.registerCommand("help");
        commandRegistry.registerCommand("exit");
        commandRegistry.registerCommand("clear");
        commandRegistry.registerCommand("set");
        commandRegistry.registerCommand("unset");
        commandRegistry.registerCommand("history");
        commandRegistry.registerCommand("source");
        commandRegistry.registerCommand("alias");
        commandRegistry.registerCommand("unalias");
        commandRegistry.registerCommand("mvn");
    }
}