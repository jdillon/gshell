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

import java.io.IOException;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to prompt a user for information.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class PromptReader
{
  private char mask = '*';

  private final LineReader reader;

  public PromptReader(final StreamSet streams, final Terminal terminal) throws IOException {
    checkNotNull(streams);
    checkNotNull(terminal);
    this.reader = createReader(streams, terminal);
  }

  protected LineReader createReader(final StreamSet streams, final Terminal terminal) throws IOException {
    checkNotNull(streams);
    checkNotNull(terminal);

    // TODO: is this optimal?  provide null history anything else for a lightweight reader

    return LineReaderBuilder.builder()
      .terminal(terminal)
      .build();
  }

  // TODO: This may be better off as a builder, with all the various configuration options
  // TODO: allow setting buffer value for initial selection

  public char getMask() {
    return mask;
  }

  public void setMask(final char mask) {
    this.mask = mask;
  }

  // TODO: Need to provide some completer function here, as well as better expect/require interface

  public String readLine(final String prompt, @Nullable final Validator validator) throws IOException {
    checkNotNull(prompt);
    String value;

    while (true) {
      value = reader.readLine(prompt);

      if (validator == null) {
        break;
      }
      else if (validator.isValid(value)) {
        break;
      }
    }

    return value;
  }

  public String readLine(final String prompt) throws IOException {
    return readLine(prompt, null);
  }

  public String readLine(final String prompt, final char mask, @Nullable final Validator validator) throws IOException {
    checkNotNull(prompt);
    String value;

    while (true) {
      value = reader.readLine(prompt, mask);

      if (validator == null) {
        break;
      }
      else if (validator.isValid(value)) {
        break;
      }
    }

    return value;
  }

  public String readLine(final String prompt, final char mask) throws IOException {
    return readLine(prompt, mask, null);
  }

  public String readPassword(final String prompt, @Nullable final Validator validator) throws IOException {
    checkNotNull(prompt);
    String value;

    while (true) {
      value = reader.readLine(prompt, mask);

      if (validator == null) {
        break;
      }
      else if (validator.isValid(value)) {
        break;
      }
    }

    return value;
  }

  public String readPassword(final String prompt) throws IOException {
    return readPassword(prompt, null);
  }

  //
  // Validator
  //

  /**
   * Allows caller to customize the validation behavior when prompting.
   */
  public interface Validator
  {
    /**
     * Determine if the given value is valid.  If the value is not valid then
     * we will prompt the user again.
     */
    boolean isValid(String value);
  }
}
