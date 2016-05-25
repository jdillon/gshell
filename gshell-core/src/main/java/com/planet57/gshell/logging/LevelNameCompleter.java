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
import javax.inject.Singleton;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

/**
 * {@link jline.console.completer.Completer} for {@link Level} names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class LevelNameCompleter
    implements Completer
{
  private final StringsCompleter delegate;

  @Inject
  public LevelNameCompleter(final LoggingSystem logging) {
    assert logging != null;
    // assume levels do not dynamically change
    delegate = new StringsCompleter();
    for (Level level : logging.getLevels()) {
      delegate.getStrings().add(level.getName());
    }
  }

  public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
    return delegate.complete(buffer, cursor, candidates);
  }
}