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

import org.apache.maven.shell.ansi.AnsiCode;
import org.apache.maven.shell.ansi.AnsiRenderer;
import org.apache.maven.shell.Command;
import org.apache.maven.shell.i18n.PrefixingMessageSource;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.AggregateMessageSource;
import org.apache.maven.shell.cli.CommandLineProcessor;
import org.apache.maven.shell.cli.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

/**
 * Support for command documenation.
 *
 * @version $Rev$ $Date$
 */
public class CommandDocumenter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Command command;

    public CommandDocumenter(final Command command) {
        assert command != null;

        this.command = command;
    }

    public void renderUsage(final PrintWriter out) {
        assert out != null;

        log.trace("Rendering command usage");

        CommandLineProcessor clp = new CommandLineProcessor();

        // Attach our helper to inject --help
        CommandHelpSupport help = new CommandHelpSupport();
        clp.addBean(help);

        // And then the beans options
        clp.addBean(command);

        // Render the help
        out.println(getDescription());
        out.println();

        Printer printer = new Printer(clp);

        AggregateMessageSource messages = new AggregateMessageSource(new MessageSource[] {
            command.getMessages(),
            help.getMessages()
        });

        printer.setMessageSource(new PrefixingMessageSource(messages, "command."));
        printer.printUsage(out, command.getName());
    }

    public void renderManual(final PrintWriter out) {
        assert out != null;

        log.trace("Rendering command manual");

        AnsiRenderer renderer = new AnsiRenderer();

        out.println(renderer.render(AnsiRenderer.encode("NAME", AnsiCode.BOLD)));
        out.print("  ");
        out.println(command.getName());
        out.println();

        out.println(renderer.render(AnsiRenderer.encode("DESCRIPTION", AnsiCode.BOLD)));
        out.print("  ");
        out.println(getDescription());
        out.println();

        //
        // TODO: Use a prefixing writer here, take the impl from shitty
        //

        out.println(renderer.render(AnsiRenderer.encode("MANUAL", AnsiCode.BOLD)));
        out.println(getManual());
        out.println();
    }

    public static final String COMMAND_DESCRIPTION = "command.description";

    public static final String COMMAND_MANUAL = "command.manual";

    public String getDescription() {
        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }

    protected String getManual() {
        return command.getMessages().getMessage(COMMAND_MANUAL);
    }
}