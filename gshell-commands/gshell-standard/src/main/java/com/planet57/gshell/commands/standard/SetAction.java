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

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.jline.Complete;
import com.planet57.gshell.variables.Variables;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Set a variable or property.
 *
 * @since 2.5
 */
@Command(name = "set", description = "Set a variable or property")
public class SetAction
    extends CommandActionSupport
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("Missing required argument: %s")
    String missingArgument(final String name);
  }

  private static final Messages messages = I18N.create(Messages.class);

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
  @Complete("variable-name")
  private List<String> values;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    if (name == null) {
      return displayList(context);
    }
    checkArgument(values != null, messages.missingArgument("VALUE"));

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
    PrintStream out = io.streams.out;

    switch (mode) {
      case PROPERTY: {
        Properties props = System.getProperties();
        props.forEach((name, value) ->
          out.format("%s='%s'%n", name, value)
        );
        break;
      }

      case VARIABLE: {
        context.getVariables().asMap().forEach((name, value) -> {
          out.format("%s='%s'", name, value);

          // When --verbose include the class details of the values
          if (verbose && value != null) {
            out.format(" (%s)", value.getClass());
          }

          out.println();
        });
        break;
      }
    }

    // force RAW stream to flush
    out.flush();

    return null;
  }
}
