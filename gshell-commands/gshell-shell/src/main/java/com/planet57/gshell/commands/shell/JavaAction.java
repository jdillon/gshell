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

import java.lang.reflect.Method;
import java.util.List;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Execute a Java standard application.
 *
 * <p>By default looks for static main(String[]) to execute, but
 * you can specify a different static method that takes a String[]
 * to execute instead.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "java", description = "Execute a Java standard application")
public class JavaAction
    extends CommandActionSupport
{
  @Option(name = "m", longName = "method", description = "Invoke a named method", token = "METHOD")
  private String methodName = "main";

  @Argument(index = 0, required = true, description = "The name of the class to invoke", token = "CLASSNAME")
  private String className;

  @Nullable
  @Argument(index = 1, description = "Arguments to pass to the METHOD of CLASSNAME", token = "ARGS")
  private List<String> args;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    log.debug("Class-name: {}", className);
    log.debug("Method-name: {}", methodName);

    Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(className);
    log.debug("Using type: {}", type);

    Method method = type.getMethod(methodName, String[].class);
    log.debug("Using method: {}", method);

    log.debug("Invoking w/arguments: {}", args);
    Object result = method.invoke(null, new Object[] { convert(args) });
    log.debug("Result: {}", result);

    return result;
  }

  private static String[] convert(@Nullable final List<?> source) {
    if (source == null) {
      return new String[0];
    }
    String[] result = new String[source.size()];
    for (int i = 0; i < source.size(); i++) {
      result[i] = String.valueOf(source.get(i));
    }
    return result;
  }
}
