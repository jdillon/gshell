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

package org.apache.maven.shell.commands.basic;

import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.notification.ExitNotification;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Exit the current shell.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Command.class, hint="exit", instantiationStrategy="per-lookup")
public class ExitCommand
    extends CommandSupport
{
    @Argument
    private int exitCode = 0;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        log.info("Exiting w/code: {}", exitCode);

        // Do not call System.exit(), ask the shell to exit instead.
        throw new ExitNotification(exitCode);
    }
}