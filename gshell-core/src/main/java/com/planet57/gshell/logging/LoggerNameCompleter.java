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
package com.planet57.gshell.logging;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.util.completer.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Completer} for {@link LoggerComponent} names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("logger-name")
@Singleton
public class LoggerNameCompleter
    extends StringsCompleter2
{
  private final LoggingSystem logging;

  @Inject
  public LoggerNameCompleter(final LoggingSystem logging) {
    this.logging = checkNotNull(logging);
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    setStrings(logging.getLoggerNames());
    super.complete(reader, commandLine, candidates);
  }
}
