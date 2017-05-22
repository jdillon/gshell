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
package com.planet57.gshell.command;

import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.planet57.gshell.command.resolver.NodePath;
import com.planet57.gshell.util.cli2.ArgumentDescriptor;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.jline.Complete;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.sonatype.goodies.common.ComponentSupport;
import org.jline.reader.Completer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides support for {@link CommandAction} implementations.
 *
 * @since 2.0
 */
public abstract class CommandActionSupport
  extends ComponentSupport
  implements CommandAction, CommandAction.NameAware, CommandAction.Prototype, CommandAction.Completable
{
  private String name;

  @Nullable
  private Completer completer;

  @Override
  public String getName() {
    checkState(name != null);
    return name;
  }

  /**
   * @see CommandAction.NameAware
   */
  @Override
  public void setName(final String name) {
    checkState(this.name == null);
    this.name = checkNotNull(name);
  }

  @Override
  public String getSimpleName() {
    return new NodePath(getName()).last();
  }

  @Nullable
  @Override
  public String getDescription() {
    Command command = getClass().getAnnotation(Command.class);
    if (command != null) {
      return command.description();
    }
    return null;
  }

  /**
   * @see CommandAction.Completable
   */
  @Override
  public Completer getCompleter() {
    if (completer == null) {
      completer = discoverCompleter();
    }
    return completer;
  }

  /**
   * All named completer instances registered with the container.  This is a dynamic sisu map.
   */
  @Inject
  private Map<String,Completer> namedCompleters;

  /**
   * Discover the completer for the command.
   *
   * @since 3.0
   */
  @Nonnull
  protected Completer discoverCompleter() {
    log.debug("Discovering completer");

    // TODO: Could probably use CliProcessorAware to avoid re-creating this
    CliProcessor cli = new CliProcessor();
    cli.addBean(this);

    List<ArgumentDescriptor> argumentDescriptors = cli.getArgumentDescriptors();
    Collections.sort(argumentDescriptors);

    if (log.isDebugEnabled()) {
      log.debug("Argument descriptors:");
      argumentDescriptors.forEach(descriptor -> log.debug("  {}", descriptor));
    }

    List<Completer> completers = new LinkedList<>();

    // attempt to resolve @Complete on each argument
    argumentDescriptors.forEach(descriptor -> {
      AccessibleObject accessible = descriptor.getSetter().getAccessible();
      if (accessible != null) {
        Complete complete = accessible.getAnnotation(Complete.class);
        if (complete != null) {
          Completer completer = namedCompleters.get(complete.value());
          checkState(completer != null, "Missing named completer: %s", complete.value());
          completers.add(completer);
        }
      }
    });

    // short-circuit if no completers detected
    if (completers.isEmpty()) {
      return NullCompleter.INSTANCE;
    }

    if (log.isDebugEnabled()) {
      log.debug("Discovered completers:");
      completers.forEach(completer -> {
        log.debug("  {}", completer);
      });
    }

    // append terminal completer for strict
    completers.add(NullCompleter.INSTANCE);

    ArgumentCompleter completer = new ArgumentCompleter(completers);
    completer.setStrict(true);
    return completer;
  }

  /**
   * @see CommandAction.Prototype
   */
  @Override
  public CommandAction create() {
    try {
      return (CommandAction) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
      "name='" + name + '\'' +
      '}';
  }
}
