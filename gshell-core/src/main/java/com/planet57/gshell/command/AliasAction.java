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

import com.planet57.gshell.util.Strings;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.util.i18n.MessageSource;
import jline.console.completer.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CommandAction} to execute an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AliasAction
    implements CommandAction, OpaqueArguments
{
  private static final Logger log = LoggerFactory.getLogger(AliasAction.class);

  private final String name;

  private final String target;

  public AliasAction(final String name, final String target) {
    this.name = checkNotNull(name);
    this.target = checkNotNull(target);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSimpleName() {
    return name;
  }

  @Override
  public Object execute(final CommandContext context) throws Exception {
    assert context != null;

    String alias = target;

    // Need to append any more arguments in the context
    Object[] args = context.getArguments();
    if (args.length > 0) {
      alias = String.format("%s %s", target, Strings.join(args, " "));
    }

    log.debug("Executing alias ({}) -> {}", getName(), alias);

    return context.getShell().execute(alias);
  }

  @Override
  public MessageSource getMessages() {
    return null;
  }

  @Override
  public Completer[] getCompleters() {
    return new Completer[0];
  }

  @Override
  public CommandAction copy() {
    try {
      return (CommandAction) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
}
