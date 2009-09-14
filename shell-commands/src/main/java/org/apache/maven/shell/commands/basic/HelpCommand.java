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
import org.apache.maven.shell.ansi.AnsiCode;
import org.apache.maven.shell.ansi.AnsiRenderer;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Display command help.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="help", instantiationStrategy="per-lookup")
public class HelpCommand
    extends CommandSupport
{
    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement
    private CommandRegistry commandRegistry;

    @Requirement(role=Completor.class, hints={"alias-name", "command-name"})
    private List<Completor> completers;

    @Argument
    private String commandName;

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
                renderManual(io.out, command);

                return Result.SUCCESS;
            }
            else if (aliasRegistry.containsAlias(commandName)) {
                io.out.print("Alias ");
                io.out.print(AnsiRenderer.encode(commandName, AnsiCode.BOLD));
                io.out.print(": ");
                io.out.println(aliasRegistry.getAlias(commandName));

                return Result.SUCCESS;
            }
            else {
                io.out.print("Command ");
                io.out.print(AnsiRenderer.encode(commandName, AnsiCode.BOLD));
                io.out.println(" not found.");

                io.out.print("Try ");
                io.out.print(AnsiRenderer.encode("help", AnsiCode.BOLD));
                io.out.println(" for a list of available commands.");

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

        // Determine the maximun name length
        int maxNameLen = 0;
        for (Command command : commands) {
            int len = command.getName().length();
            maxNameLen = Math.max(len, maxNameLen);
        }

        IO io = context.getIo();
        io.out.println("Available commands:");
        for (Command command : commands) {
            String formattedName = String.format("%-" + maxNameLen + "s", command.getName());
            String desc = getDescription(command);

            io.out.print("  ");
            io.out.print(AnsiRenderer.encode(formattedName, AnsiCode.BOLD));

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

    public static final String COMMAND_DESCRIPTION = "command.description";

    public static final String COMMAND_MANUAL = "command.manual";

    public String getDescription(final Command command) {
        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }

    protected String getManual(final Command command) {
        return command.getMessages().getMessage(COMMAND_MANUAL);
    }

    public void renderManual(final PrintWriter out, final Command command) {
        assert out != null;

        log.trace("Rendering command manual");

        AnsiRenderer renderer = new AnsiRenderer();

        out.println(renderer.render(AnsiRenderer.encode("NAME", AnsiCode.BOLD)));
        out.print("  ");
        out.println(command.getName());
        out.println();

        out.println(renderer.render(AnsiRenderer.encode("DESCRIPTION", AnsiCode.BOLD)));
        out.print("  ");
        out.println(getDescription(command));
        out.println();

        //
        // TODO: Use a prefixing writer here, take the impl from shitty
        //

        out.println(renderer.render(AnsiRenderer.encode("MANUAL", AnsiCode.BOLD)));
        out.println(getManual(command));
        out.println();
    }
}
