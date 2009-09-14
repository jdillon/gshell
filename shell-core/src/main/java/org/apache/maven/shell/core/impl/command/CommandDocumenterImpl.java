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

package org.apache.maven.shell.core.impl.command;

import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.cli.Printer;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandDocumenter;
import org.apache.maven.shell.i18n.AggregateMessageSource;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.PrefixingMessageSource;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.PrefixingStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.plexus.component.annotations.Component;

/**
 * The default {@link CommandDocumenter} component.
 *
 * @version $Rev$ $Date$
 */
@Component(role=CommandDocumenter.class)
public class CommandDocumenterImpl
    implements CommandDocumenter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String COMMAND_DESCRIPTION = "command.description";

    private static final String COMMAND_MANUAL = "command.manual";

    public String getDescription(final Command command) {
        assert command != null;

        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }

    public String getManual(final Command command) {
        assert command != null;
        
        return command.getMessages().getMessage(COMMAND_MANUAL);
    }

    public void renderUsage(final Command command, final IO io) {
        assert command != null;
        assert io != null;

        log.trace("Rendering command usage");

        CommandLineProcessor clp = new CommandLineProcessor();

        // Attach our helper to inject --help
        CommandHelpSupport help = new CommandHelpSupport();
        clp.addBean(help);

        // And then the beans options
        clp.addBean(command);

        // Render the help
        io.out.println(getDescription(command));
        io.out.println();

        Printer printer = new Printer(clp);

        AggregateMessageSource messages = new AggregateMessageSource(new MessageSource[] {
            command.getMessages(),
            help.getMessages()
        });

        printer.setMessageSource(new PrefixingMessageSource(messages, "command."));
        printer.printUsage(io.out, command.getName());
    }

    public void renderManual(final Command command, final IO io) {
        assert command != null;
        assert io != null;

        log.trace("Rendering command manual");

        PrefixingStream prefixed = new PrefixingStream("   ", io.outputStream);

        io.out.println("@|bold NAME|");
        io.out.print("  ");
        prefixed.println(command.getName());
        io.out.println();

        io.out.println("@|bold DESCRIPTION|");
        io.out.print("  ");
        prefixed.println(getDescription(command));
        io.out.println();

        io.out.println("@|bold MANUAL|");

        prefixed.println(getManual(command));
        io.out.println();
    }
}