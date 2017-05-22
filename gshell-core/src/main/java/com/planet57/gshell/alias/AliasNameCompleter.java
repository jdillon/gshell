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
package com.planet57.gshell.alias;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.util.jline.DynamicCompleter;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.util.jline.Candidates.candidate;

/**
 * {@link Completer} for alias names.
 *
 * @since 2.5
 */
@Named("alias-name")
@Singleton
public class AliasNameCompleter
  extends DynamicCompleter
  implements EventAware
{
  private final StringsCompleter2 delegate = new StringsCompleter2();

  private final AliasRegistry aliases;

  @Inject
  public AliasNameCompleter(final AliasRegistry aliases) {
    this.aliases = checkNotNull(aliases);
  }

  @Override
  protected void init() {
    aliases.getAliases().forEach((name, target) -> {
      delegate.add(name, candidate(name, target));
    });
  }

  @Override
  protected Collection<Candidate> getCandidates() {
    return delegate.getCandidates();
  }

  @Subscribe
  void on(final AliasRegisteredEvent event) {
    String name = event.getName();
    delegate.add(name, candidate(name, event.getAlias()));
  }

  @Subscribe
  void on(final AliasRemovedEvent event) {
    delegate.remove(event.getName());
  }
}
