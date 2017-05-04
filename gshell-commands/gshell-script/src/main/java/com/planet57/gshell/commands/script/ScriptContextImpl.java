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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.planet57.gshell.command.IO;

/**
 * Custom {@link ScriptContext}.
 *
 * @since 3.0
 */
@Named
@Singleton
public class ScriptContextImpl
  extends SimpleScriptContext
{
  public ScriptContextImpl(final ScriptEngine engine, final IO io) {
    checkNotNull(engine);
    setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);

    checkNotNull(io);
    setReader(io.in);
    setWriter(io.out);
    setErrorWriter(io.err);
    set("io", io);
  }

  public void set(final String name, final Object value) {
    setAttribute(name, value, ScriptContext.ENGINE_SCOPE);
  }
}
