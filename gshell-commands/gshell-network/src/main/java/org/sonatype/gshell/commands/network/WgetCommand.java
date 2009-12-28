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

import org.codehaus.plexus.util.IOUtil;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.io.Flusher;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.util.cli.Option;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * Fetch a file from a URL.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
@Command(name="wget")
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

        log.debug("Fetching: {}", source);
        URLConnection conn = source.openConnection();
        InputStream in = conn.getInputStream();

        String contentType = conn.getContentType();
        log.debug("Content type: {}", contentType);

        OutputStream out;
        if (outputFile != null) {
            log.debug("Writing to file: {}", outputFile);
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
        }
        else {
            out = io.streams.out;
        }

        IOUtil.copy(in, out);

        if (outputFile != null) {
            Closer.close(out);
            return outputFile;
        }
        else {
            Flusher.flush(out);
        }
        
        return Result.SUCCESS;
    }
}