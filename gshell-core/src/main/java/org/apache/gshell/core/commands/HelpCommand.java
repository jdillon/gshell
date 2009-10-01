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

package org.apache.gshell.core.commands;

import jline.Completor;
import org.apache.gshell.cli.Argument;
import org.apache.gshell.command.CommandAction;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.command.CommandDocumenter;
import org.apache.gshell.command.Command;
import org.apache.gshell.io.IO;
import org.apache.gshell.registry.AliasRegistry;
import org.apache.gshell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Display command help.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Command
@Component(role=HelpCommand.class)
public class HelpCommand
    extends CommandActionSupport
{
    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement
    private CommandRegistry commandRegistry;

    @Requirement
    private CommandDocumenter commandDocumeter;

    @Requirement(role=Completor.class, hints={"alias-name", "command-name"})
    private List<Completor> installCompleters;

    @Argument
    private String commandName;

    public HelpCommand() {}

    @Override
    public Completor[] getCompleters() {
        if (super.getCompleters() == null) {
            setCompleters(installCompleters);
        }

        return super.getCompleters();
    }
    
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (commandName == null) {
            return displayAvailableCommands(context);
        }
        else {
            // TODO: Use the resolver
            
            if (commandRegistry.containsCommand(commandName)) {
                CommandAction command = commandRegistry.getCommand(commandName);
                commandDocumeter.renderManual(command, io);

                return Result.SUCCESS;
            }
            else if (aliasRegistry.containsAlias(commandName)) {
                io.out.println(getMessages().format("info.explain-alias", commandName, aliasRegistry.getAlias(commandName)));
                return Result.SUCCESS;
            }
            else {
                io.out.println(getMessages().format("info.command-not-found", commandName));
                return Result.FAILURE;
            }
        }
    }

    private Object displayAvailableCommands(final CommandContext context) throws Exception {
        assert context != null;

        log.debug("Listing brief help for commands");

        Collection<CommandAction> commands = new LinkedList<CommandAction>();
        for (String name : commandRegistry.getCommandNames()) {
            commands.add(commandRegistry.getCommand(name));
        }

        // Determine the maximum name length
        int maxNameLen = 0;
        for (CommandAction command : commands) {
            int len = command.getName().length();
            maxNameLen = Math.max(len, maxNameLen);
        }
        String nameFormat = "%-" + maxNameLen + 's';

        IO io = context.getIo();
        io.out.println(getMessages().format("info.available-commands"));
        for (CommandAction command : commands) {
            String formattedName = String.format(nameFormat, command.getName());
            String desc = commandDocumeter.getDescription(command);

            io.out.format("  @|bold %s|", formattedName);
            if (desc != null) {
                io.out.print("  ");
                io.out.println(desc);
            }
            else {
                io.out.println();
            }
        }

        return Result.SUCCESS;
    }
}
