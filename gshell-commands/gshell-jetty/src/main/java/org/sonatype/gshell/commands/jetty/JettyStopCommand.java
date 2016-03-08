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
package org.sonatype.gshell.commands.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.LifeCycle;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.util.pref.Preferences;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Start a <a href="http://eclipse.org/jetty">Jetty</a> server.
 *
 * @since 2.6.5
 */
@Command(name = "jetty-stop")
@Preferences(path = "commands/jetty-stop")
public class JettyStopCommand
    extends CommandActionSupport
{
    public Object execute(final CommandContext context) throws Exception {
        checkNotNull(context);

        LifeCycle lc = context.getVariables().get(Server.class);
        if (lc != null) {
            lc.stop();
            context.getVariables().unset(Server.class);
            return true;
        }
        return false;
    }
}