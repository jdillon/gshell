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

import org.sonatype.goodies.common.ComponentSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to load shell scripts.
 *
 * @since 3.0
 */
public class ShellScriptLoader
  extends ComponentSupport
{
  private boolean loadProfileScripts = true;

  private boolean loadInteractiveScripts = true;

  public boolean isLoadProfileScripts() {
    return loadProfileScripts;
  }

  public void setLoadProfileScripts(final boolean enable) {
    this.loadProfileScripts = enable;
  }

  public boolean isLoadInteractiveScripts() {
    return loadInteractiveScripts;
  }

  public void setLoadInteractiveScripts(final boolean enable) {
    this.loadInteractiveScripts = enable;
  }

  public void loadProfileScripts(final Shell shell) throws Exception {
    checkNotNull(shell);

    if (!isLoadProfileScripts()) {
      return;
    }

    String fileName = shell.getBranding().getProfileScriptName();
    loadSharedScript(shell, fileName);
    loadUserScript(shell, fileName);
  }

  public void loadInteractiveScripts(final Shell shell) throws Exception {
    checkNotNull(shell);

    if (!isLoadInteractiveScripts()) {
      return;
    }

    String fileName = shell.getBranding().getInteractiveScriptName();
    loadSharedScript(shell, fileName);
    loadUserScript(shell, fileName);
  }

  private void loadScript(final Shell shell, final File file) throws Exception {
    checkNotNull(file);
    log.debug("Loading script: {}", file);

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        shell.execute(line);
      }
    }
  }

  private void loadUserScript(final Shell shell, final String fileName) throws Exception {
    checkNotNull(fileName);
    File file = new File(shell.getBranding().getUserContextDir(), fileName);
    if (file.exists()) {
      loadScript(shell, file);
    }
    else {
      log.trace("User script is not present: {}", file);
    }
  }

  private void loadSharedScript(final Shell shell, final String fileName) throws Exception {
    checkNotNull(fileName);
    File file = new File(shell.getBranding().getShellContextDir(), fileName);
    if (file.exists()) {
      loadScript(shell, file);
    }
    else {
      log.trace("Shared script is not present: {}", file);
    }
  }
}
