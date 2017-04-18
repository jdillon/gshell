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
package com.planet57.gshell.command.support;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.shell.ShellHolder;
import com.planet57.gshell.util.pref.PreferenceProcessor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper for command preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandPreferenceSupport
{
  public static PreferenceProcessor createProcessor(final CommandAction command) {
    checkNotNull(command);

    PreferenceProcessor pp = new PreferenceProcessor();
    Branding branding = ShellHolder.get().getBranding();
    pp.setBasePath(branding.getPreferencesBasePath());
    pp.addBean(command);

    return pp;
  }
}
