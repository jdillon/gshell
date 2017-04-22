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
package com.planet57.gshell.commands.standard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.io.FileAssert;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.io.Closeables;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Read and execute commands from a file in the current shell.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "source")
public class SourceCommand
    extends CommandActionSupport
{
  @Argument(required = true)
  private String path;

  @Inject
  public SourceCommand installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Shell shell = context.getShell();

    URL url;
    try {
      url = new URL(path);
    }
    catch (MalformedURLException e) {
      url = new File(path).toURI().toURL();
    }

    BufferedReader reader = openReader(url);
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        shell.execute(line);
      }
    }
    finally {
      Closeables.close(reader);
    }

    return Result.SUCCESS;
  }

  private BufferedReader openReader(final Object source) throws IOException {
    BufferedReader reader;

    if (source instanceof File) {
      File file = (File) source;
      log.debug("Using source file: {}", file);

      new FileAssert(file).exists().isFile().isReadable();

      reader = new BufferedReader(new FileReader(file));
    }
    else if (source instanceof URL) {
      URL url = (URL) source;
      log.debug("Using source URL: {}", url);

      reader = new BufferedReader(new InputStreamReader(url.openStream()));
    }
    else {
      String tmp = String.valueOf(source);

      // First try a URL
      try {
        URL url = new URL(tmp);
        log.debug("Using source URL: {}", url);

        reader = new BufferedReader(new InputStreamReader(url.openStream()));
      }
      catch (MalformedURLException e) {
        // They try a file
        File file = new File(tmp);
        log.debug("Using source file: {}", file);

        reader = new BufferedReader(new FileReader(tmp));
      }
    }

    return reader;
  }
}
