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

import org.sonatype.goodies.common.ComponentSupport;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Script manager.
 *
 * @since 3.0
 */
@Named
@Singleton
public class ScriptManager
  extends ComponentSupport
{
  private ScriptEngineManager engineManager;

  public ScriptManager() {
    engineManager = new ScriptEngineManager(ClassLoader.getSystemClassLoader());

    // TODO: adapt guice registered factories

    engineManager.getEngineFactories().forEach(factory -> {
      log.debug("Engine-factory: {} v{}; language={}, version={}, names={}, mime-types={}, extensions={}",
        factory.getEngineName(),
        factory.getEngineVersion(),
        factory.getLanguageName(),
        factory.getLanguageVersion(),
        factory.getNames(),
        factory.getMimeTypes(),
        factory.getExtensions()
      );
    });
  }

  public ScriptEngine engineForLanguage(final String language) {
    checkNotNull(language);

    log.debug("Resolving engine for language: {}", language);
    ScriptEngine engine = engineManager.getEngineByName(language);
    checkState(engine != null, "Missing engine for language: %s", language);

    return engine;
  }
}
