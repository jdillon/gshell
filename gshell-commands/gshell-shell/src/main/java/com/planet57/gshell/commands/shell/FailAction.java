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
package com.planet57.gshell.commands.shell;

import com.google.common.annotations.VisibleForTesting;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;

import javax.annotation.Nonnull;

/**
 * Fail with an exception.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "fail", description = "Fail with an exception")
public class FailAction
    extends CommandActionSupport
{
  @Argument(description = "Failure message", token = "TEXT")
  private String message = "Failed";

  enum Type
  {
    exception,
    runtime,
    error
  }

  @Option(name="t", longName = "type", description = "Fail with specific throwable type", token = "TYPE")
  private Type type = Type.exception;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    switch (type) {
      case exception:
        throw new FailException(message);
      case runtime:
        throw new FailRuntimeException(message);
      case error:
        throw new FailError(message);
    }
    // unreachable
    throw new Error();
  }

  @VisibleForTesting
  static class FailException
      extends Exception
  {
    FailException(final String message) {
      super(message);
    }
  }

  @VisibleForTesting
  static class FailRuntimeException
    extends RuntimeException
  {
    FailRuntimeException(final String message) {
      super(message);
    }
  }

  @VisibleForTesting
  static class FailError
    extends Error
  {
    FailError(final String message) {
      super(message);
    }
  }
}
