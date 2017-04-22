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
package com.planet57.gshell.shell;

import javax.inject.Inject;
import javax.inject.Named;

import org.jline.reader.Completer;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;

/**
 * Shell {@link Completer}.
 *
 * @since 3.0
 */
@Named("main")
public class ShellCompleter
  extends ArgumentCompleter
{
  @Inject
  public ShellCompleter(final @Named("alias-name") Completer alias,
                        final @Named("command-name") Completer command)
  {
    super(new AggregateCompleter(alias, command), NullCompleter.INSTANCE);
  }
}
