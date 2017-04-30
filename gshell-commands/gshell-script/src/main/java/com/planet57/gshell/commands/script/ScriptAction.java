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
package com.planet57.gshell.commands.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.script.ScriptEngine;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.io.Closeables;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;

import static com.google.common.base.Preconditions.checkState;

/**
 * Execute a script.
 *
 * @since 3.0
 */
@Command(name = "script")
@Preferences(path = "commands/script")
public class ScriptAction
    extends CommandActionSupport
{
  @Inject
  private ScriptManager scriptManager;

  @Preference
  @Option(name = "l", longName = "language", required = true)
  private String language;

  @Option(name = "f", longName = "file")
  @Nullable
  private File file;

  @Option(name = "u", longName = "url")
  @Nullable
  private URL url;

  @Option(name = "e", longName = "expression")
  @Nullable
  private String expression;

  @Argument
  @Nullable
  private List<String> args;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    // validate only 1 source option has been specified
    int sources = 0;
    if (file != null) sources++;
    if (url != null) sources++;
    if (expression != null) sources++;
    checkState(sources == 1, "Only one of file, url or expression may be specified");

    ScriptEngine engine = scriptManager.engineForLanguage(language);
    log.debug("Engine: {}", engine);

    ScriptContextImpl scriptContext = new ScriptContextImpl(engine, context.getIo());
    scriptContext.set("context", context);
    scriptContext.set("log", log);
    scriptContext.set("args", args);

    Reader source = createSource();
    try {
      log.debug("Source: {}", source);
      Object result = engine.eval(source, scriptContext);
      log.debug("Result: {}", result);

      return result;
    }
    finally {
      Closeables.close(source);
    }
  }

  private Reader createSource() throws Exception {
    if (file != null) {
      return new BufferedReader(new FileReader(file));
    }
    else if (url != null) {
      return new BufferedReader(new InputStreamReader(url.openStream()));
    }
    else if (expression != null) {
      return new StringReader(expression);
    }
    // should never happen
    throw new Error();
  }
}
