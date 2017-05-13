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
package com.planet57.gshell.util.io;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to prompt user for information.
 *
 * @since 3.0
 */
public class PromptHelper
{
  private LineReader lineReader;

  public PromptHelper(final Terminal terminal) {
    checkNotNull(terminal);
    this.lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .build();
  }

  public String readLine() {
    return lineReader.readLine();
  }

  public String readLine(final String prompt) {
    checkNotNull(prompt);
    return lineReader.readLine(prompt);
  }

  public String readLine(final String prompt, final Character mask) {
    checkNotNull(prompt);
    checkNotNull(mask);
    return lineReader.readLine(prompt, mask);
  }

  /**
   * Ask a question which results in a boolean result.
   */
  public boolean askBoolean(final String question) {
    checkNotNull(question);
    String result = readLine(String.format("%s (yes/no): ", question));
    return result.equalsIgnoreCase("yes");
  }
}
