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
package com.planet57.gshell.testharness;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.History;
import org.sonatype.goodies.lifecycle.LifecycleSupport;

/**
 * Dummy {@link Shell}.
 *
 * @since 3.0
 */
public class DummyShell
  extends LifecycleSupport
  implements Shell
{
  public Branding getBranding() {
    return null;
  }

  public IO getIo() {
    return null;
  }

  public Variables getVariables() {
    return null;
  }

  public History getHistory() {
    return null;
  }

  public boolean isOpened() {
    return false;
  }

  public void close() {
    // empty
  }

  public Object execute(CharSequence line) throws Exception {
    return null;
  }

  public Object execute(CharSequence command, Object[] args) throws Exception {
    return null;
  }

  public Object execute(Object... args) throws Exception {
    return null;
  }

  public boolean isInteractive() {
    return false;
  }

  public void run(Object... args) throws Exception {
    // empty
  }
}
