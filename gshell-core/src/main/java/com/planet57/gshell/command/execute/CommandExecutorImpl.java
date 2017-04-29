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
package com.planet57.gshell.command.execute;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Throwables;
import com.planet57.gshell.command.ErrorNotification;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.shell.Shell;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.sonatype.goodies.common.ComponentSupport;
import com.planet57.gshell.variables.Variables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CommandExecutor}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Named
@Singleton
public class CommandExecutorImpl
  extends ComponentSupport
  implements CommandExecutor
{
  // HACK: have to use impl here, as CommandProcessor does not expose enough api

  private final CommandProcessorImpl commandProcessor;

  @Inject
  public CommandExecutorImpl(final CommandProcessorImpl commandProcessor) {
    this.commandProcessor = checkNotNull(commandProcessor);
  }

  @Override
  @Nullable
  public Object execute(final Shell shell, final String line) throws Exception {
    checkNotNull(shell);
    checkNotNull(line);

    if (line.trim().length() == 0) {
      log.trace("Ignoring empty line");
      return null;
    }

    IO io = shell.getIo();
    CommandSessionImpl session = commandProcessor.createSession(io.streams.in, io.streams.out, io.streams.err);
    session.put(".shell", shell);

    // HACK: stuff all variables into session, this is not ideal however
    Variables variables = shell.getVariables();
    variables.names().forEach(name ->
      session.getVariables().put(name, variables.get(name))
    );

    // FIXME: this doesn't appear to do the trick; because "echo" will resolve to function "echo" :-(
    // disable trace output by default
    session.put("echo", null);

    try {
      return session.execute(line);
    }
    catch (ErrorNotification n) {
      // FIXME: this is left over from ExecutingVisitor and need to see how gogo may use/need this or drop it
      Throwable cause = n.getCause();
      Throwables.propagateIfPossible(cause, Exception.class, Error.class);
      // should normally never happen
      throw n;
    }
  }
}
