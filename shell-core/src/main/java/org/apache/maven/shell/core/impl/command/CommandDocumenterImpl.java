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

import org.apache.maven.shell.ShellContextHolder;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.ansi.AnsiRenderer;
import org.apache.maven.shell.cli.Printer;
import org.apache.maven.shell.cli.Processor;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandDocumenter;
import org.apache.maven.shell.i18n.AggregateMessageSource;
import org.apache.maven.shell.i18n.PrefixingMessageSource;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.io.PrefixingOutputStream;
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
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandDocumenter.class)
public class CommandDocumenterImpl
    implements CommandDocumenter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String COMMAND_DESCRIPTION = "command.description";

    private static final String COMMAND_MANUAL = "command.manual";

    private String interpolate(final Command command, final String text) {
        assert command != null;
        assert text != null;

        if (text.indexOf("${") != -1) {
            Interpolator interp = new StringSearchInterpolator();
            interp.addValueSource(new PrefixedObjectValueSource("command", command));
            interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
            interp.addValueSource(new AbstractValueSource(false) {
                public Object getValue(final String expression) {
                    Variables vars = ShellContextHolder.get().getVariables();
                    return vars.get(expression);
                }
            });

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
        printer.setMessageSource(new PrefixingMessageSource(messages, "command."));
        printer.printUsage(io.out, command.getName());
    }

    public void renderManual(final Command command, final IO io) {
        assert command != null;
        assert io != null;

        log.trace("Rendering command manual");

        PrintStream out = new PrintStream(new PrefixingOutputStream(io.outputStream, "   "));
        AnsiRenderer renderer = new AnsiRenderer();

        //
        // HACK: PrefixingOutputStream has a problem with state and using io.out, so we have to
        //       add more println()s to compensate for now
        //

        io.out.println("@|bold NAME|");
        io.out.println();
        out.println(command.getName());
        io.out.println();

        String text;

        io.out.println("@|bold DESCRIPTION|");
        text = getDescription(command);
        text = renderer.render(text);
        out.println();
        out.println(text);
        io.out.println();
        
        io.out.println("@|bold MANUAL|");
        text = getManual(command);
        text = renderer.render(text);
        out.println();
        out.println(text);
        io.out.println();
    }
}