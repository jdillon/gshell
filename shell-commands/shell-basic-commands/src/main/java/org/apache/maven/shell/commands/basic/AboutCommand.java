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

import org.apache.maven.shell.Branding;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Display information about the current shell.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Command.class, hint="about")
public class AboutCommand
    extends CommandSupport
{
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        Branding branding = context.getShell().getBranding();

        if (io.isVerbose()) {
            io.verbose("Version: {}", branding.getVersion());
            io.verbose("Display Name: {}", branding.getDisplayName());
            io.verbose("Program Name: {}", branding.getProgramName());
            io.verbose("Script Extension: {}", branding.getScriptExtension());
            io.verbose("Script Home Dir: {}", branding.getShellHomeDir());
            io.verbose("Script Context Dir: {}", branding.getShellContextDir());
            io.verbose("Script User Home Dir: {}", branding.getUserHomeDir());
            io.verbose("Script User Context Dir: {}", branding.getUserContextDir());
        }
        else {
            io.info(branding.getAboutMessage());
        }

        return Result.SUCCESS;
    }
}