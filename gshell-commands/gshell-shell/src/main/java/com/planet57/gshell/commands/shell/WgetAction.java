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
package com.planet57.gshell.commands.shell;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.io.ByteStreams;
import com.google.common.io.Flushables;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.io.Closeables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fetch a file from a URL.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
@Command(name = "wget")
public class WgetAction
    extends CommandActionSupport
{
  @Option(name = "v", longName = "verbose")
  private boolean verbose;

  @Nullable
  @Option(name = "o", longName = "output-file")
  private File outputFile;

  @Argument(required = true)
  private URL source;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    io.out.printf("Downloading: %s%n", source);
    if (verbose) {
      io.out.printf("Connecting to: %s:%s%n", source.getHost(),
          source.getPort() != -1 ? source.getPort() : source.getDefaultPort());
    }

    URLConnection conn = source.openConnection();

    if (verbose) {
      io.out.printf("Length: %s [%s]%n", conn.getContentLength(), conn.getContentType());
    }

    InputStream in = conn.getInputStream();

    OutputStream out;
    if (outputFile != null) {
      if (verbose) {
        io.out.printf("Saving to file: %s%n", outputFile);
      }
      out = new BufferedOutputStream(new FileOutputStream(outputFile));
    }
    else {
      out = io.streams.out;
    }

    ByteStreams.copy(in, out);

    // if we write a file, close it then return the file
    if (outputFile != null) {
      Closeables.close(out);
      io.out.printf("Saved %s [%s]%n", outputFile, outputFile.length());
      return outputFile;
    }

    // else flush the stream and say we did good
    Flushables.flushQuietly(out);
    return null;
  }
}
