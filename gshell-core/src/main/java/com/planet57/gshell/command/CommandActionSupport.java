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

import java.util.Arrays;
import java.util.stream.Collectors;

import com.planet57.gshell.command.resolver.NodePath;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.sonatype.goodies.common.ComponentSupport;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides support for {@link CommandAction} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class CommandActionSupport
  extends ComponentSupport
  implements CommandAction, CommandAction.NameAware, CommandAction.Prototype, CommandAction.Completable
{
  private String name;

  private Completer completer = NullCompleter.INSTANCE;

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

  /**
   * @see CommandAction.Completable
   */
  @Override
  public Completer getCompleter() {
    return completer;
  }

  /**
   * Install raw completer.
   *
   * @since 3.0
   */
  protected void setCompleter(final Completer completer) {
    this.completer = checkNotNull(completer);
  }

  /**
   * Install argument completer for the given completers.
   *
   * This will handle translating {@code null} members of completers into {@link NullCompleter#INSTANCE}.
   */
  protected void setCompleters(final Completer... completers) {
    checkNotNull(completers);
    completer = new ArgumentCompleter(
      // translate null to NullCompleter.INSTANCE
      Arrays.stream(completers).map(it -> it == null ? NullCompleter.INSTANCE : it).collect(Collectors.toList())
    );
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
    return "CommandActionSupport{" +
      "name='" + name + '\'' +
      '}';
  }
}
