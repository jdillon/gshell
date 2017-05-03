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
import org.jline.utils.AttributedString;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to generate {@link Candidate} instances.
 *
 * @since 3.0
 */
public class Candidates
{
  private Candidates() {
    // empty
  }

  public static Candidate candidate(final String value) {
    checkNotNull(value);
    return new Candidate(AttributedString.stripAnsi(value), value, null, null, null, null, true);
  }

  public static Candidate candidate(final String name, @Nullable final String description) {
    checkNotNull(name);
    return new Candidate(AttributedString.stripAnsi(name), name, null, description, null, null, true);
  }
}
