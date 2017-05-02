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
package com.planet57.gshell.commands.standard;

import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Set a variable or property.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "set", description = "Set a variable or property.")
public class SetAction
    extends CommandActionSupport
{
  enum Mode
  {
    VARIABLE,
    PROPERTY
  }

  @Option(name = "m", longName = "mode", description = "Set mode", token = "MODE")
  private Mode mode = Mode.VARIABLE;

  @Option(name = "v", longName = "verbose", description = "Enable verbose output")
  private boolean verbose;

  @Nullable
  @Argument(index = 0, description = "Variable or property name", token = "NAME")
  private String name;

  @Nullable
  @Argument(index = 1, description = "Variable or property value or expression to evaluate", token = "VALUE")
  private List<String> values;

  @Inject
  public SetAction installCompleters(@Named("variable-name") final Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
    return this;
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    MessageSource messages = getMessages();

    if (name == null) {
      return displayList(context);
    }
    checkArgument(values != null, getMessages().format("error.missing-arg", messages.getMessage("command.argument.values.token")));

    String value = String.join(" ", values);

    switch (mode) {
      case PROPERTY:
        log.debug("Setting system property: {}={}", name, value);
        System.setProperty(name, value);
        break;

      case VARIABLE:
        Variables vars = context.getVariables();
        log.debug("Setting variable: {}={}", name, value);
        vars.set(name, value);
        break;
    }

    return null;
  }

  private Object displayList(final CommandContext context) throws Exception {
    IO io = context.getIo();

    // using RAW io.stream.out to avoid any ANSI encoding

    switch (mode) {
      case PROPERTY: {
        Properties props = System.getProperties();

        for (Object o : props.keySet()) {
          String name = (String) o;
          String value = props.getProperty(name);
          io.streams.out.printf("%s='%s'%n", name, value);
        }
        break;
      }

      case VARIABLE: {
        Variables variables = context.getVariables();
        for (String name : variables.names()) {
          Object value = variables.get(name);
          io.streams.out.printf("%s='%s'", name, value);

          // When --verbose include the class details of the values
          if (verbose && value != null) {
            io.streams.out.printf(" (%s)", value.getClass());
          }

          io.streams.out.println();
        }
        break;
      }
    }

    // force RAW stream to flush
    io.streams.out.flush();

    return null;
  }
}
