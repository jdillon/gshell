/*
 * Copyright (c) 2009-present the original author or authors.
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
package com.planet57.gshell.help;

import java.io.PrintWriter;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.command.support.CommandHelpSupport;
import com.planet57.gshell.command.support.CommandPreferenceSupport;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.PrintBuffer;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.util.pref.PreferenceDescriptor;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.fusesource.jansi.AnsiRenderer;

/**
 * {@link HelpPage} for a command.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandHelpPage
    implements HelpPage
{
    private final Node node;

    private final HelpContentLoader loader;

    private final CommandAction command;

    public CommandHelpPage(final Node node, final HelpContentLoader loader) {
        assert node != null;
        assert !node.isGroup();
        this.node = node;
        assert loader != null;
        this.loader = loader;
        command = node.getAction();
    }

    public String getName() {
        return node.getAction().getSimpleName();
    }

    public String getDescription() {
        return CommandHelpSupport.getDescription(node.getAction());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    // Public so that ObjectBasedValueSource can access (it really should set accessible so this is not needed)
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
        public String getAbsoluteName() {
            return Node.ROOT + command.getName();
        }

        @SuppressWarnings("unused")
        public String getSimpleName() {
            return command.getSimpleName();
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return CommandHelpSupport.getDescription(command);
        }

        private void printHeader(final PrintBuffer buff, final String name) {
            buff.format("@|bold %s|@", getMessages().format(name)).println();
            buff.println();
        }

        //
        // FIXME: The indent on the results for arguments+options is 2, not 4 (should be consistent)
        //
        
        @SuppressWarnings("unused")
        public String getArguments() {
            if (clp.getArgumentDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();
            printHeader(buff, "section.arguments");
            printer.printArguments(buff, clp.getArgumentDescriptors());

            return buff.toString();
        }

        @SuppressWarnings("unused")
        public String getOptions() {
            if (clp.getOptionDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();
            printHeader(buff, "section.options");
            printer.printOptions(buff, clp.getOptionDescriptors());

            return buff.toString();
        }

        @SuppressWarnings("unused")
        public String getPreferences() {
            if (pp.getDescriptors().isEmpty()) {
                return "";
            }

            PrintBuffer buff = new PrintBuffer();
            printHeader(buff, "section.preferences");

            for (PreferenceDescriptor pd : pp.getDescriptors()) {
                String text = String.format("    %s @|bold %s|@ (%s)",
                    pd.getPreferences().absolutePath(), pd.getId(), pd.getSetter().getType().getSimpleName());
                buff.println(AnsiRenderer.render(text));
            }

            return buff.toString();
        }

        @SuppressWarnings("unused")
        public String getDetails() {
            // This ugly muck adds a newline as needed if the last section was not empty
            // and the current section is not empty, so that the page looks correct.
            
            PrintBuffer buff = new PrintBuffer();
            String content, last;

            content = getOptions();
            buff.append(content);
            last = content;

            content = getArguments();
            if (content.length() !=0 && last.length() !=0) {
                buff.println();
            }
            buff.append(content);
            last = content;

            content = getPreferences();
            if (content.length() !=0 && last.length() !=0) {
                buff.println();
            }
            buff.append(content);

            // newline is already in the help stream

            return buff.toString();
        }
    }

    public void render(final PrintWriter out) {
        assert out != null;

        //
        // FIXME: Really need a little bit more of a help page language here to simplify the formatting of things
        //
        
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