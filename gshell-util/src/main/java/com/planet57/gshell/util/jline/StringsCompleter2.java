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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.util.jline.Candidates.candidate;

/**
 * Customized strings completer which provides mutable candidates.
 *
 * @since 3.0
 */
public class StringsCompleter2
  implements Completer
{
  private final Map<String,Candidate> candidates = new LinkedHashMap<>();

  public StringsCompleter2() {
    // empty
  }

  /**
   * Set completion strings; replacing any existing.
   */
  public void set(final String... strings) {
    checkNotNull(strings);
    set(Arrays.asList(strings));
  }

  /**
   * Set completion strings; replacing any existing.
   */
  public void set(final Iterable<String> strings) {
    checkNotNull(strings);
    candidates.clear();
    addAll(strings);
  }

  /**
   * Add all strings to existing candidates.
   */
  public void addAll(final Iterable<String> strings) {
    checkNotNull(strings);
    for (String string : strings) {
      add(string);
    }
  }

  /**
   * Add all strings to existing candidates.
   */
  public void addAll(final String... strings) {
    checkNotNull(strings);
    addAll(Arrays.asList(strings));
  }

  /**
   * Add a string to existing candidates.
   */
  public void add(final String string) {
    checkNotNull(string);
    candidates.put(string, candidate(string));
  }

  /**
   * Remove string from candidates.
   */
  public void remove(final String string) {
    checkNotNull(string);
    candidates.remove(string);
  }

  public void add(final String string, final Candidate candidate) {
    checkNotNull(string);
    checkNotNull(candidate);
    candidates.put(string, candidate);
  }

  /**
   * Returns all configured candidate strings.
   */
  public Collection<String> getStrings() {
    return candidates.keySet();
  }

  /**
   * Returns all configured candidates.
   */
  public Collection<Candidate> getCandidates() {
    return candidates.values();
  }

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    checkNotNull(candidates);
    candidates.addAll(getCandidates());
  }
}
