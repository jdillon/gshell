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
package com.planet57.gshell.help;

import com.planet57.gshell.shell.Shell;

import java.io.PrintWriter;

/**
 * Represents the content of a help page.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public interface HelpPage
{
  String getName();

  String getDescription();

  // TODO: Section?

  // FIXME: Could probably use CommandContext instead of Shell here?
  // FIXME: ... as this is only used by HelpAction; various bits of context are needed

  /**
   * @since 3.0
   */
  void render(Shell shell, PrintWriter out) throws Exception;
}
