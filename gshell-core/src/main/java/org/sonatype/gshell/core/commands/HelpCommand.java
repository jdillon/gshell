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

package org.sonatype.gshell.core.commands;

import com.google.inject.Inject;
import jline.console.completers.AggregateCompleter;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.CommandDocumenter;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.core.command.CommandActionSupport;
import org.sonatype.gshell.core.completer.AliasNameCompleter;
import org.sonatype.gshell.core.completer.CommandNameCompleter;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistry;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Display command help.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Command
public class HelpCommand
    extends CommandActionSupport
{
    private AliasRegistry aliasRegistry;

    private CommandRegistry commandRegistry;

    private CommandDocumenter commandDocumeter;

    @Argument
    private String commandName;

    @Inject
    public HelpCommand(final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry, final CommandDocumenter commandDocumeter) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
        assert commandDocumeter != null;
        this.commandDocumeter = commandDocumeter;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    @Inject
    public HelpCommand installCompleters(final AliasNameCompleter c1, final CommandNameCompleter c2) {
        assert c1 != null;
        setCompleters(new AggregateCompleter(c1, c2), null);
        return this;
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
