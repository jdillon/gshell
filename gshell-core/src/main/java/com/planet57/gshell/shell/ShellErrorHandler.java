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

import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;

import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Shell error-handler which renders errors with ANSI codes.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellErrorHandler
{
  private enum Messages
  {
    ERROR_AT,
    ERROR_CAUSED_BY,
    ERROR_LOCATION_NATIVE,
    ERROR_LOCATION_UNKNOWN;

    private static final MessageSource messages = new ResourceBundleMessageSource(ShellErrorHandler.class);

    String format(final Object... args) {
      return messages.format(name(), args);
    }
  }

  /**
   * @since 3.0
   */
  public boolean handleError(final PrintWriter out, final Throwable error, final boolean verbose) {
    checkNotNull(out);
    checkNotNull(error);
    displayError(out, error, verbose);
    return true;
  }

  private void displayError(final PrintWriter out, final Throwable error, final boolean verbose) {
    // TODO: use Throwables2.explain(), or mimic same style with ANSI support when showTrace == false

    Throwable cause = error;

    out.format("@|bold,red %s|@", cause.getClass().getName());
    if (cause.getMessage() != null) {
      out.format(": @|bold, red %s|@", cause.getMessage());
    }
    out.println();

    if (verbose) {
      while (cause != null) {
        for (StackTraceElement e : cause.getStackTrace()) {
          out.format("     @|bold %s|@ %s.%s (@|bold %s|@)%n",
            Messages.ERROR_AT.format(),
            e.getClassName(),
            e.getMethodName(),
            getLocation(e)
          );
        }

        cause = cause.getCause();
        if (cause != null) {
          out.format("@|bold %s|@ %s%n", Messages.ERROR_CAUSED_BY.format(), cause.getClass().getName());
        }
      }
    }

    out.flush();
  }

  private String getLocation(final StackTraceElement e) {
    assert e != null;

    if (e.isNativeMethod()) {
      return Messages.ERROR_LOCATION_NATIVE.format();
    }
    else if (e.getFileName() == null) {
      return Messages.ERROR_LOCATION_UNKNOWN.format();
    }
    else if (e.getLineNumber() >= 0) {
      return String.format("%s:%s", e.getFileName(), e.getLineNumber());
    }
    else {
      return e.getFileName();
    }
  }
}
