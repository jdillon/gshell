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

package org.sonatype.gshell.command.help;

import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

/**
 * Helper to inject <tt>--help<tt> support.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandHelpSupport
{
    ///CLOVER:OFF

    @Option(name = "h", longName = "help", override=true)
    public boolean displayHelp;

//    FIXME: Need to resolve how to deal with this
//    @Option(name = "--", handler = StopHandler.class)
    public boolean stop;

    private MessageSource messages;

    public MessageSource getMessages() {
        if (messages == null) {
            messages = new ResourceBundleMessageSource(getClass());
        }

        return messages;
    }
}