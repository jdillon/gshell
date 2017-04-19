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
package com.planet57.gshell.shell;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Flushables;
import jline.console.history.FileHistory;

/**
 * Implementation of {@link History} for <a href="http://jline.sf.net">JLine</a>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellHistory
    extends FileHistory
    implements History
{
  public ShellHistory(final File file) throws IOException {
    super(file);

    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run() {
        Flushables.flushQuietly(ShellHistory.this);
      }
    });
  }
}
