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
package com.planet57.gshell.command.registry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.planet57.gshell.event.EventAware;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Completer} for command names.
 * Keeps up to date automatically by handling command-related events.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("command-name")
@Singleton
public class CommandNameCompleter
  extends StringsCompleter2
  implements EventAware
{
  private final CommandRegistry commands;

  @Inject
  public CommandNameCompleter(final CommandRegistry commands) {
    this.commands = checkNotNull(commands);
  }

  @Override
  protected void init() {
    set(commands.getCommandNames());
  }

  @Subscribe
  void on(final CommandRegisteredEvent event) {
    add(event.getName());
  }

  @Subscribe
  void on(final CommandRemovedEvent event) {
    remove(event.getName());
  }
}
