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
package org.sonatype.gshell.command.support;

import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.util.cli2.CliProcessor;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.util.i18n.AggregateMessageSource;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.PrefixingMessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

/**
 * Helper to inject <tt>--help<tt> support.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandHelpSupport
{
    public static final String COMMAND_DOT = "command.";

    public static final String COMMAND_NAME = "command.name";
    
    public static final String COMMAND_DESCRIPTION = "command.description";

    @Option(name = "h", longName = "help", override=true)
    public boolean displayHelp;

    private MessageSource messages;

    private MessageSource getMessages() {
        if (messages == null) {
            messages = new ResourceBundleMessageSource(getClass());
        }

        return messages;
    }

    public CliProcessor createProcessor(final CommandAction command) {
        assert command != null;

        CliProcessor clp = new CliProcessor();
        clp.addBean(command);
        clp.addBean(this);

        AggregateMessageSource messages = new AggregateMessageSource(command.getMessages(), this.getMessages());
        clp.setMessages(new PrefixingMessageSource(messages, COMMAND_DOT));

        return clp;
    }

    public static String getDescription(final CommandAction command) {
        assert command != null;
        
        return command.getMessages().getMessage(COMMAND_DESCRIPTION);
    }
}