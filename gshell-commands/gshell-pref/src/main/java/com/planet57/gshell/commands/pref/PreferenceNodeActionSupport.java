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
package com.planet57.gshell.commands.pref;

import java.util.prefs.Preferences;

import com.planet57.gshell.util.cli2.Argument;

/**
 * Support for preference node commands.
 *
 * @since 2.0
 */
public abstract class PreferenceNodeActionSupport
    extends PreferenceActionSupport
{
  @Argument(index = 0, required = true, description = "Preference path", token = "PATH")
  private String path;

  protected Preferences node() throws Exception {
    Preferences root = root();
    log.debug("Root: {}", root);

    Preferences node = root.node(path);
    log.debug("Node: {}", node);

    return node;
  }
}
