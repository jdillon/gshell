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
package com.planet57.gshell.internal.completer;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.functions.FunctionSetRegisteredEvent;
import com.planet57.gshell.functions.FunctionSetRemovedEvent;
import com.planet57.gshell.util.jline.DynamicCompleter;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;

import static com.planet57.gshell.util.jline.Candidates.candidate;

/**
 * {@link Completer} for function names.
 *
 * @since 3.0
 */
@Named("function-name")
@Singleton
public class FunctionNameCompleter
  extends DynamicCompleter
  implements EventAware
{
  private final StringsCompleter2 delegate = new StringsCompleter2();

  @Override
  protected Collection<Candidate> getCandidates() {
    return delegate.getCandidates();
  }

  @Subscribe
  void on(final FunctionSetRegisteredEvent event) {
    for (String name : event.getFunctions().names()) {
      delegate.add(name, candidate(name));
    }
  }

  @Subscribe
  void on(final FunctionSetRemovedEvent event) {
    for (String name : event.getFunctions().names()) {
      delegate.remove(name);
    }
  }
}
