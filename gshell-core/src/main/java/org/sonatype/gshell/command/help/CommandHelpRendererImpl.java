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

package org.sonatype.gshell.command.help;

import com.google.inject.Inject;
import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandDocumenter;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.PrintBuffer;
import org.sonatype.gshell.util.ReplacementParser;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.HelpPrinter;
import org.sonatype.gshell.util.i18n.AggregateMessageSource;
import org.sonatype.gshell.util.i18n.PrefixingMessageSource;
import org.sonatype.gshell.util.pref.PreferenceDescriptor;
import org.sonatype.gshell.util.pref.PreferenceProcessor;
import org.sonatype.gshell.vars.Variables;

import java.io.PrintWriter;

import static org.sonatype.gshell.command.CommandDocumenter.*;

/**
 * {@link CommandHelpRenderer} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.4
 */
public class CommandHelpRendererImpl
    implements CommandHelpRenderer
{
    private final CommandHelpLoader loader;

    @Inject
    public CommandHelpRendererImpl(final CommandHelpLoader loader) {
        assert loader != null;
        this.loader = loader;
    }

    public void render(final CommandAction command, final PrintWriter out) {
        assert command != null;
        assert out != null;

        String text;
        try {
            text = loader.load(command);
            text = evaluate(command, text);
            out.println(text);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String evaluate(final CommandAction command, final String input) {
        if (input.contains("@{")) {
            final CliProcessor clp = new CliProcessor();
            clp.addBean(command);
            CommandHelpSupport help = new CommandHelpSupport();
            clp.addBean(help);
            AggregateMessageSource messages = new AggregateMessageSource(command.getMessages(), help.getMessages());
            final HelpPrinter printer = new HelpPrinter(clp);
            printer.addMessages(new PrefixingMessageSource(messages, COMMAND_DOT));

            final PreferenceProcessor pp = new PreferenceProcessor();
            Branding branding = ShellHolder.get().getBranding();
            pp.setBasePath(branding.getPreferencesBasePath());
            pp.addBean(command);

            ReplacementParser parser = new ReplacementParser("\\@\\{([^}]+)\\}")
            {
                @Override
                protected Object replace(final String key) throws Exception {
                    Object rep = null;
                    if (key.equals(COMMAND_NAME)) {
                        rep = command.getName();
                    }
                    else if (key.equals(COMMAND_DESCRIPTION)) {
                        rep = command.getMessages().getMessage(COMMAND_DESCRIPTION);
                    }
                    else if (key.equals("command.arguments")) {
                        if (!clp.getArgumentDescriptors().isEmpty()) {
                            PrintBuffer buff = new PrintBuffer();

                            buff.println("@|bold ARGUMENTS|@"); // TODO: i18n
                            buff.println();

                            printer.printArguments(buff, clp.getArgumentDescriptors());

                            rep = buff.toString();
                        }
                    }
                    else if (key.equals("command.options")) {
                        if (!clp.getOptionDescriptors().isEmpty()) {
                            PrintBuffer buff = new PrintBuffer();

                            buff.println("@|bold OPTIONS|@"); // TODO: i18n
                            buff.println();

                            printer.printOptions(buff, clp.getOptionDescriptors());

                            rep = buff.toString();
                        }
                    }
                    else if (key.equals("command.preferences")) {
                        if (!pp.getDescriptors().isEmpty()) {
                            PrintBuffer buff = new PrintBuffer();

                            buff.println("@|bold PREFERENCES|@"); // TODO: i18n
                            buff.println();

                            for (PreferenceDescriptor pd : pp.getDescriptors()) {
                                String text = String.format("    %s @|bold %s|@ (%s)", pd.getPreferences().absolutePath(), pd.getId(), pd.getSetter().getType().getSimpleName());
                                buff.println(AnsiRenderer.render(text));
                            }

                            rep = buff.toString();
                        }
                    }

                    if (rep == null) {
                        Variables vars = ShellHolder.get().getVariables();
                        rep = vars.get(key);
                    }
                    if (rep == null) {
                        rep = System.getProperty(key);
                    }

                    return rep;
                }
            };

            return parser.parse(input);
        }

        return input;
    }
}