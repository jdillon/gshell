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

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Customized strings completer which provides mutable candidates.
 *
 * @since 3.0
 */
public class StringsCompleter2
  implements Completer
{
  private static final Logger log = LoggerFactory.getLogger(StringsCompleter2.class);

  private final Map<String,Candidate> candidates = new LinkedHashMap<>();

  private boolean initialized = false;

  public StringsCompleter2() {
    // empty
  }

  /**
   * Set completion strings; replacing any existing.
   */
  public void setStrings(final String... strings) {
    checkNotNull(strings);
    setStrings(Arrays.asList(strings));
  }

  /**
   * Set completion strings; replacing any existing.
   */
  public void setStrings(final Iterable<String> strings) {
    checkNotNull(strings);
    candidates.clear();
    addStrings(strings);
  }

  public void addStrings(final Iterable<String> strings) {
    checkNotNull(strings);
    for (String string : strings) {
      addString(string);
    }
  }

  public void addString(final String string) {
    checkNotNull(string);
    candidates.put(string, candidate(string));
  }

  public void removeString(final String string) {
    checkNotNull(string);
    candidates.remove(string);
  }

  /**
   * Invoked first time a completion occurs.
   */
  protected void init() {
    // empty
  }

  /**
   * Invoked before candidates are applied.
   */
  protected void prepare() {
    // empty
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    checkNotNull(candidates);

    if (!initialized) {
      init();
      initialized = true;
    }
    prepare();

    candidates.addAll(this.candidates.values());
  }

  //
  // Helpers
  //

  /**
   * Returns an ANSI-enabled candidate for given string.
   */
  public static Candidate candidate(final String value) {
    log.trace("Creating candidate: {}", value);
    return new Candidate(AttributedString.stripAnsi(value), value, null, null, null, null, true);
  }
}
