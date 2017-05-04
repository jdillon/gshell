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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.sonatype.goodies.common.ComponentSupport;

import com.google.common.base.Throwables;

/**
 * Adds logging around wrapped {@link Completer} delegate.
 *
 * @since 3.0
 */
public class LoggingCompleter
  extends ComponentSupport
  implements Completer
{
  private final Completer delegate;

  public LoggingCompleter(final Completer delegate) {
    this.delegate = checkNotNull(delegate);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine line, final List<Candidate> candidates) {
    log.trace("Complete: reader={}, line={}, candidates={}", reader, line, candidates);
    try {
      delegate.complete(reader, line, candidates);
    }
    catch (Exception e) {
      // FIXME: this is required in part due to: https://github.com/jline/jline3/issues/115
      log.warn("Completer failed", e);
      Throwables.propagateIfPossible(e, RuntimeException.class);
      throw new RuntimeException(e);
    }
  }
}
