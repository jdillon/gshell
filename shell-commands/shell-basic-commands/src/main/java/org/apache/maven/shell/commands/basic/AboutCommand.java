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
import org.apache.maven.shell.cli.Option;
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
    @Option(name="-v", aliases={"--verbose"})
    private boolean verbose;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();
        Branding branding = context.getShell().getBranding();

        if (verbose || io.isVerbose()) {
            io.info("Version: {}", branding.getVersion());
            io.info("Display Name: {}", branding.getDisplayName());
            io.info("Program Name: {}", branding.getProgramName());
            io.info("Script Extension: {}", branding.getScriptExtension());
            io.info("Script Home Dir: {}", branding.getShellHomeDir());
            io.info("Script Context Dir: {}", branding.getShellContextDir());
            io.info("Script User Home Dir: {}", branding.getUserHomeDir());
            io.info("Script User Context Dir: {}", branding.getUserContextDir());
        }
        else {
            io.info(branding.getAboutMessage());
        }

        return Result.SUCCESS;
    }
}