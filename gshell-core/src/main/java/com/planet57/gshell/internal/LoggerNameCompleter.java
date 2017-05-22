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
package com.planet57.gshell.internal;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.planet57.gshell.logging.LoggerComponent;
import com.planet57.gshell.logging.LoggingSystem;
import com.planet57.gshell.util.jline.DynamicCompleter;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;

import java.util.Collection;

/**
 * {@link Completer} for {@link LoggerComponent} names.
 *
 * @since 2.5
 */
@Named("logger-name")
@Singleton
public class LoggerNameCompleter
    extends DynamicCompleter
{
  private final StringsCompleter2 delegate = new StringsCompleter2();

  @Nullable
  private final LoggingSystem logging;

  @Inject
  public LoggerNameCompleter(@Nullable final LoggingSystem logging) {
    this.logging = logging;
  }

  /**
   * Re-adjusted completions each attempt to complete; loggers could change dynamically.
   */
  @Override
  protected void prepare() {
    if (logging != null) {
      delegate.set(logging.getLoggerNames());
    }
  }

  @Override
  protected Collection<Candidate> getCandidates() {
    return delegate.getCandidates();
  }
}
