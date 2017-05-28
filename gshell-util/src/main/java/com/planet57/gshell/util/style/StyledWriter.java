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
package com.planet57.gshell.util.style;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ???
 *
 * @since 3.0
 */
public class StyledWriter
    extends PrintWriter
{
  private final Terminal terminal;

  private final StyleExpression expression;

  public StyledWriter(final Writer out, final Terminal terminal, final StyleResolver resolver, final boolean autoFlush) {
    super(out, autoFlush);
    this.terminal = checkNotNull(terminal);
    this.expression = new StyleExpression(resolver);
  }

  public StyledWriter(final OutputStream out, final Terminal terminal, final StyleResolver resolver, final boolean autoFlush) {
    super(out, autoFlush);
    this.terminal = checkNotNull(terminal);
    this.expression = new StyleExpression(resolver);
  }

  @Override
  public void write(@Nonnull final String value) {
    AttributedString result = expression.evaluate(value);
    super.write(result.toAnsi(terminal));
  }

  // Prevent partial output from being written while formatting or we will get rendering exceptions

  @Override
  public PrintWriter format(@Nonnull final String format, final Object... args) {
    print(String.format(format, args));
    return this;
  }

  @Override
  public PrintWriter format(final Locale locale, @Nonnull final String format, final Object... args) {
    print(String.format(locale, format, args));
    return this;
  }
}
