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
package com.planet57.gshell.util.jline;

import org.jline.builtins.Less;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link Terminal} helpers.
 *
 * @since 3.0
 */
public class TerminalHelper
{
  private TerminalHelper() {
    // empty
  }

  // Adapted from: https://github.com/apache/felix/blob/trunk/gogo/jline/src/main/java/org/apache/felix/gogo/jline/Posix.java

  public static void printColumns(final Terminal terminal, final PrintWriter out, final Stream<String> values, final boolean horizontal) {
    int width = terminal.getWidth();
    List<AttributedString> strings = values.map(AttributedString::fromAnsi).collect(Collectors.toList());

    if (!strings.isEmpty()) {
      int max = strings.stream().mapToInt(AttributedString::columnLength).max().orElse(0);
      int c = Math.max(1, width / max);
      while (c > 1 && c * max + (c - 1) >= width) {
        c--;
      }

      int columns = c;
      int lines = (strings.size() + columns - 1) / columns;
      IntBinaryOperator index;

      if (horizontal) {
        index = (i, j) -> i * columns + j;
      }
      else {
        index = (i, j) -> j * lines + i;
      }

      AttributedStringBuilder buff = new AttributedStringBuilder();
      for (int i = 0; i < lines; i++) {
        for (int j = 0; j < columns; j++) {
          int idx = index.applyAsInt(i, j);
          if (idx < strings.size()) {
            AttributedString str = strings.get(idx);
            boolean hasRightItem = j < columns - 1 && index.applyAsInt(i, j + 1) < strings.size();
            buff.append(str);
            if (hasRightItem) {
              for (int k = 0; k <= max - str.length(); k++) {
                buff.append(' ');
              }
            }
          }
        }
        buff.append('\n');
      }

      out.print(buff.toAnsi(terminal));
    }
  }

  public static void pageOutput(final Terminal terminal, @Nullable final String name, final String output)
    throws IOException, InterruptedException
  {
    Less less = new Less(terminal);
    less.run(new InputStreamSource(new ByteArrayInputStream(output.getBytes()), name));
  }

  public static void pageOutput(final Terminal terminal, final String output)
    throws IOException, InterruptedException
  {
    pageOutput(terminal, null, output);
  }
}
