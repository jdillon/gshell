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
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    @Option(name="o", longName="output-file")
    private File outputFile;

    @Argument(required=true)
    private URL source;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        io.info("Downloading: {}", source); // TODO: i18n
        io.verbose("Connecting to: {}:{}", source.getHost(), source.getPort() != -1 ? source.getPort() : source.getDefaultPort()); // TODO: i18n
        
        URLConnection conn = source.openConnection();

        io.verbose("Length: {} [{}]", conn.getContentLength(), conn.getContentType()); // TODO: i18n

        InputStream in = conn.getInputStream();

        OutputStream out;
        if (outputFile != null) {
            io.verbose("Saving to file: {}", outputFile); // TODO: i18n
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
        }
        else {
            out = io.streams.out;
        }

        IOUtil.copy(in, out);

        // if we write a file, close it then return the file
        if (outputFile != null) {
            Closer.close(out);
            io.info("Saved {} [{}]",outputFile, outputFile.length()); // TODO: i18n
            return outputFile;
        }

        // else flush the stream and say we did good
        Flusher.flush(out);
        return Result.SUCCESS;
    }
}