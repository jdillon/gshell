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

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.History;
import org.jline.terminal.Terminal;
import org.sonatype.goodies.lifecycle.Lifecycle;

/**
 * Provides access to execute commands.
 *
 * @since 2.0
 */
public interface Shell
  extends Lifecycle
{
  Branding getBranding();

  /**
   * @since 3.0
   */
  Terminal getTerminal();

  Variables getVariables();

  History getHistory();

  Object execute(CharSequence line) throws Exception;

  void run() throws Exception;
}
