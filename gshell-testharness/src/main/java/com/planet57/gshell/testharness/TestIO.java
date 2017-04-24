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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.io.StreamSet;

/**
 * Test {@link IO}.
 *
 * @since 3.0
 */
public class TestIO
    extends IO
{
  private ByteArrayOutputStream output;

  private ByteArrayOutputStream error;

  public TestIO() {
    this(new ByteArrayOutputStream(), new ByteArrayOutputStream());
  }

  public TestIO(final ByteArrayOutputStream output, final ByteArrayOutputStream error) {
    super(new StreamSet(System.in, new PrintStream(output), new PrintStream(error)), true);
    this.output = output;
    this.error = error;
  }

  public ByteArrayOutputStream getOutput() {
    return output;
  }

  public String getOutputString() {
    return new String(getOutput().toByteArray());
  }

  public ByteArrayOutputStream getError() {
    return error;
  }

  public String getErrorString() {
    return new String(getError().toByteArray());
  }
}
