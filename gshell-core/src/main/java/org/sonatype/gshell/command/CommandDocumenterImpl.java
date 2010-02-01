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

package org.sonatype.gshell.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.HelpPrinter;
import org.sonatype.gshell.util.i18n.AggregateMessageSource;
import org.sonatype.gshell.util.i18n.PrefixingMessageSource;

/**
 * The default {@link CommandDocumenter} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Deprecated
public class CommandDocumenterImpl
    implements CommandDocumenter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public String getDescription(final CommandAction command) {
        assert command != null;

        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }

    public void renderUsage(final CommandAction command, final IO io) {
        assert command != null;
        assert io != null;

        CliProcessor clp = new CliProcessor();

        // Attach our helper to inject --help
        CommandHelpSupport help = new CommandHelpSupport();
        clp.addBean(help);

        // And then the beans options
        clp.addBean(command);

        // Render the help
        io.out.println(getDescription(command));
        io.out.println();

        HelpPrinter printer = new HelpPrinter(clp);
        AggregateMessageSource messages = new AggregateMessageSource(command.getMessages(), help.getMessages());
        printer.addMessages(new PrefixingMessageSource(messages, COMMAND_DOT));
        printer.printUsage(io.out, command.getName());
    }
}