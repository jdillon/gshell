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

package org.apache.maven.shell.commands.basic;

import jline.Completor;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandDocumenter;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Display command help.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Command.class, hint="help")
public class HelpCommand
    extends CommandSupport
{
    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement
    private CommandRegistry commandRegistry;

    @Requirement
    private CommandDocumenter commandDocumeter;

    @Requirement(role=Completor.class, hints={"alias-name", "command-name"})
    private List<Completor> completers;

    @Argument
    private String commandName;

    public HelpCommand() {}

    @Override
    public Completor[] getCompleters() {
        assert completers != null;

        return new Completor[] {
            new AggregateCompleter(completers),
            null
        };
    }
    
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (commandName == null) {
            return displayAvailableCommands(context);
        }
        else {
            if (commandRegistry.containsCommand(commandName)) {
                Command command = commandRegistry.getCommand(commandName);
                commandDocumeter.renderManual(command, io);

                return Result.SUCCESS;
            }
            else if (aliasRegistry.containsAlias(commandName)) {
                io.out.println(getMessages().format("info.explain-alias", "@|bold " + commandName + '|', commandName));
                return Result.SUCCESS;
            }
            else {
                io.out.println(getMessages().format("info.command-not-found", "@|bold " + commandName + '|', "help"));
                return Result.FAILURE;
            }
        }
    }

    private Object displayAvailableCommands(final CommandContext context) throws Exception {
        assert context != null;

        log.debug("Listing brief help for commands");

        Collection<Command> commands = new LinkedList<Command>();
        for (String name : commandRegistry.getCommandNames()) {
            commands.add(commandRegistry.getCommand(name));
        }

        // Determine the maximum name length
        int maxNameLen = 0;
        for (Command command : commands) {
            int len = command.getName().length();
            maxNameLen = Math.max(len, maxNameLen);
        }
        String nameFormat = "%-" + maxNameLen + 's';

        IO io = context.getIo();
        io.out.println(getMessages().format("info.available-commands"));
        for (Command command : commands) {
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
