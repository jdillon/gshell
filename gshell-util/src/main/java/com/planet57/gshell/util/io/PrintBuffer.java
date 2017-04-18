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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Printable String buffer.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class PrintBuffer
    extends PrintWriter
{
  public PrintBuffer() {
    super(new StringWriter(), true);
  }

  public StringBuffer getBuffer() {
    return ((StringWriter) out).getBuffer();
  }

  public void reset() {
    getBuffer().setLength(0);
  }

  public String toString() {
    return getBuffer().toString();
  }
}
