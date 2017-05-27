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
package com.planet57.gshell.commands.pref

import java.util.prefs.Preferences

import com.planet57.gshell.testharness.CommandTestSupport

/**
 * Support for preference action tests.
 */
abstract class PreferenceActionTestSupport
    extends CommandTestSupport
{
  static {
    System.setProperty('java.util.prefs.PreferencesFactory', 'org.sonatype.goodies.prefs.memory.MemoryPreferencesFactory')
  }

  protected final String ID = "test-${System.currentTimeMillis()}"

  /**
   * Preferences path for tests to use.
   */
  protected final String PATH = "/test/${ID}/${this.class.package.name.replace('.', '/')}"

  PreferenceActionTestSupport(final Class<?> type) {
    super(type)
  }

  protected Preferences userRoot() {
    return Preferences.userRoot()
  }

  protected Preferences systemRoot() {
    return Preferences.systemRoot()
  }
}
