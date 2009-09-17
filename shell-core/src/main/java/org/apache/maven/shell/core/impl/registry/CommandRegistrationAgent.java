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

package org.apache.maven.shell.core.impl.registry;

import org.apache.maven.shell.command.CommandException;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers commands in order.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandRegistrationAgent.class)
public class CommandRegistrationAgent
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private CommandRegistry commandRegistry;

    public void registerCommands() throws CommandException {
        log.debug("Registering commands");

        String[] names = {
            "help",
            "about",
            "exit",
            "clear",
            "set",
            "unset",
            "history",
            "recall",
            "source",
            "alias",
            "unalias",
            "echo",
            "ls",
            "cd",
            "pwd",
            "mvn",
            "test/puke",
        };

        for (String name : names) {
            commandRegistry.registerCommand(name);
        }
    }
}