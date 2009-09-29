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

package org.apache.gshell.core.command;

import org.apache.gshell.ShellHolder;
import org.apache.gshell.Variables;
import org.apache.gshell.ansi.AnsiRenderer;
import org.apache.gshell.cli.Printer;
import org.apache.gshell.cli.Processor;
import org.apache.gshell.command.Command;
import org.apache.gshell.command.CommandDocumenter;
import org.apache.gshell.i18n.AggregateMessageSource;
import org.apache.gshell.i18n.MessageSource;
import org.apache.gshell.i18n.PrefixingMessageSource;
import org.apache.gshell.i18n.ResourceBundleMessageSource;
import org.apache.gshell.io.IO;
import org.apache.gshell.io.PrefixingOutputStream;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * The default {@link CommandDocumenter} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Component(role= CommandDocumenter.class)
public class CommandDocumenterImpl
    implements CommandDocumenter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    private String interpolate(final Command command, final String text) {
        assert command != null;
        assert text != null;

        if (text.contains(StringSearchInterpolator.DEFAULT_START_EXPR)) {
            Interpolator interp = new StringSearchInterpolator();
            interp.addValueSource(new PrefixedObjectValueSource(COMMAND, command));
            interp.addValueSource(new AbstractValueSource(false) {
                public Object getValue(final String expression) {
                    Variables vars = ShellHolder.get().getVariables();
                    return vars.get(expression);
                }
            });
            interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

            try {
                return interp.interpolate(text);
            }
            catch (InterpolationException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return text;
        }
    }

    public String getDescription(final Command command) {
        assert command != null;

        String text = command.getMessages().getMessage(COMMAND_DESCRIPTION);
        return interpolate(command, text);
    }

    public String getManual(final Command command) {
        assert command != null;
        
        String text = command.getMessages().getMessage(COMMAND_MANUAL);
        return interpolate(command, text);
    }

    public void renderUsage(final Command command, final IO io) {
        assert command != null;
        assert io != null;

        log.trace("Rendering command usage");

        Processor clp = new Processor();

        // Attach our helper to inject --help
        CommandHelpSupport help = new CommandHelpSupport();
        clp.addBean(help);

        // And then the beans options
        clp.addBean(command);

        // Render the help
        io.out.println(getDescription(command));
        io.out.println();

        Printer printer = new Printer(clp);
        AggregateMessageSource messages = new AggregateMessageSource(command.getMessages(), help.getMessages());
        printer.addMessages(new PrefixingMessageSource(messages, COMMAND_DOT));
        printer.printUsage(io.out, command.getName());
    }

    public void renderManual(final Command command, final IO io) {
        assert command != null;
        assert io != null;

        log.trace("Rendering command manual");

        PrintStream out = new PrintStream(new PrefixingOutputStream(io.streams.out, "   "));
        AnsiRenderer renderer = new AnsiRenderer();

        //
        // HACK: PrefixingOutputStream has a problem with state and using io.out, so we have to
        //       add more println()s to compensate for now
        //

        io.out.format("@|bold %s|", messages.getMessage("section.name")).println();
        io.out.println();
        out.println(command.getName());
        io.out.println();

        String text;

        io.out.format("@|bold %s|", messages.getMessage("section.description")).println();
        text = getDescription(command);
        text = renderer.render(text);
        out.println();
        out.println(text);
        io.out.println();

        String manual = getManual(command);
        if (manual != null && manual.trim().length() != 0) {
            io.out.format("@|bold %s|", messages.getMessage("section.manual")).println();
            text = manual;
            text = renderer.render(text);
            out.println();
            out.println(text);
            io.out.println();
        }
    }
}