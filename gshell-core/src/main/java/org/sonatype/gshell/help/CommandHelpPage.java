/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.help;

import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandHelpSupport;
import org.sonatype.gshell.command.CommandPreferenceSupport;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.PrintBuffer;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.HelpPrinter;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.util.pref.PreferenceDescriptor;
import org.sonatype.gshell.util.pref.PreferenceProcessor;

import java.io.PrintWriter;

/**
 * {@link HelpPage} for a command.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandHelpPage
    implements HelpPage
{
    private final CommandAction command;

    private final HelpContentLoader loader;

    public CommandHelpPage(final CommandAction command, final HelpContentLoader loader) {
        assert command != null;
        this.command = command;
        assert loader != null;
        this.loader = loader;
    }

    public String getName() {
        return command.getName();
    }

    public String getDescription() {
        return CommandHelpSupport.getDescription(command);
    }

    // Public so that ObjectBasedValueSource can access
    public class Helper
    {
        private final CliProcessor clp;

        private final HelpPrinter printer;

        private final PreferenceProcessor pp;

        private MessageSource messages;

        public Helper() {
            CommandHelpSupport help = new CommandHelpSupport();
            clp = help.createProcessor(command);
            printer = new HelpPrinter(clp);
            pp = CommandPreferenceSupport.createProcessor(command);
        }

        private MessageSource getMessages() {
            if (messages == null) {
                messages = new ResourceBundleMessageSource(getClass());
            }

            return messages;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return command.getName();
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return CommandHelpSupport.getDescription(command);
        }

        @SuppressWarnings("unused")
        public String getArguments() {
            if (clp.getArgumentDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();

            buff.format("@|bold %s|@", getMessages().format("section.arguments")).println();
            buff.println();

            printer.printArguments(buff, clp.getArgumentDescriptors());

            return buff.toString();
        }

        @SuppressWarnings("unused")
        public String getOptions() {
            if (clp.getOptionDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();

            buff.format("@|bold %s|@", getMessages().format("section.options")).println();
            buff.println();

            printer.printOptions(buff, clp.getOptionDescriptors());

            return buff.toString();
        }

        @SuppressWarnings("unused")
        public String getPreferences() {
            if (pp.getDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();

            buff.format("@|bold %s|@", getMessages().format("section.preferences")).println();
            buff.println();

            for (PreferenceDescriptor pd : pp.getDescriptors()) {
                String
                    text =
                    String.format("    %s @|bold %s|@ (%s)", pd.getPreferences().absolutePath(), pd.getId(), pd.getSetter().getType().getSimpleName());
                buff.println(AnsiRenderer.render(text));
            }

            return buff.toString();
        }
    }

    public void render(final PrintWriter out) {
        assert out != null;

        Interpolator interp = new StringSearchInterpolator("@{", "}");
        interp.addValueSource(new PrefixedObjectValueSource("command.", new Helper()));
        interp.addValueSource(new PrefixedObjectValueSource("branding.", ShellHolder.get().getBranding()));
        interp.addValueSource(new AbstractValueSource(false)
        {
            public Object getValue(final String expression) {
                return ShellHolder.get().getVariables().get(expression);
            }
        });
        interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

        try {
            String text = loader.load(command.getClass().getName(), command.getClass().getClassLoader());
            out.println(interp.interpolate(text));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}