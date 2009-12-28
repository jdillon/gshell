/*
 * Copyright (C) 2009 the original author or authors.
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

package org.sonatype.gshell.commands.network;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.util.cli.Option;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

/**
 * ???
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
@Command(name="hostname")
public class WgetCommand
    extends CommandActionSupport
{
    @Argument(required=true)
    private URL source;

    @Option(name="-o", aliases={"--output-file"}, argumentRequired=true)
    private File outputFile;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        // TODO:

        return Result.SUCCESS;
    }
}