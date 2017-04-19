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
package com.planet57.gshell.variables;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.event.EventAware;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link jline.console.completer.Completer} for variable names.
 * Keeps up to date automatically by handling variable-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named("variable-name")
@Singleton
public class VariableNameCompleter
    implements Completer, EventAware
{
  private final Provider<Variables> variables;

  private final StringsCompleter delegate = new StringsCompleter();

  private boolean initialized;

  @Inject
  public VariableNameCompleter(final Provider<Variables> variables) {
    this.variables = checkNotNull(variables);
  }

  private void init() {
    // Prime the delegate with any existing variable names
    Iterator<String> iter = variables.get().names();
    while (iter.hasNext()) {
      delegate.getStrings().add(iter.next());
    }
    initialized = true;
  }

  public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
    if (!initialized) {
      init();
    }

    return delegate.complete(buffer, cursor, candidates);
  }

  @Subscribe
  void on(final VariableSetEvent event) {
    delegate.getStrings().add(event.getName());
  }

  @Subscribe
  void on(final VariableUnsetEvent event) {
    delegate.getStrings().remove(event.getName());
  }
}
