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
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
                renderManual(io, command);

                return Result.SUCCESS;
            }
            else if (aliasRegistry.containsAlias(commandName)) {
                io.out.print("Alias @|bold " + commandName + "|: "); // TODO: i18n
                io.out.println(aliasRegistry.getAlias(commandName));

                return Result.SUCCESS;
            }
            else {
                io.out.println("Command @|bold " + commandName + "| not found."); // TODO: i18n
                io.out.println("Try @|bold help| for a list of available commands."); // TODO: i18n

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

        IO io = context.getIo();
        io.out.println("Available commands:"); // TODO: i18n
        for (Command command : commands) {
            String formattedName = String.format("%-" + maxNameLen + "s", command.getName());
            String desc = getDescription(command);

            io.out.print("  @|bold " + formattedName + "|");
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

    //
    // HACK: CommandDocumenter is not accessible in this context
    //

    private Object createDocumenter(final Command command) throws Exception {
        assert command != null;

        Class type = Thread.currentThread().getContextClassLoader().loadClass("org.apache.maven.shell.core.impl.command.CommandDocumenter");
        Constructor factory = type.getConstructor(Command.class);
        return factory.newInstance(command);
    }

    private String getDescription(final Command command) throws Exception {
        assert command != null;

        Object doc = createDocumenter(command);
        Method method = doc.getClass().getMethod("getDescription");
        return (String)method.invoke(doc);
    }

    private void renderManual(final IO io, final Command command) throws Exception {
        assert io != null;
        assert command != null;

        Object doc = createDocumenter(command);
        Method method = doc.getClass().getMethod("renderManual", IO.class);
        method.invoke(doc, io);
    }
}
