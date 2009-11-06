/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell;

import java.io.File;

/**
 * Defines the basic elements for branding a shell.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public interface Branding
{
    String getDisplayName();

    String getProgramName();

    String getScriptExtension();

    String getVersion();

    String getAboutMessage();

    String getWelcomeMessage();

    String getGoodbyeMessage();

    String getPrompt();

    String getProfileScriptName();

    String getInteractiveScriptName();

    String getHistoryFileName();

    File getShellHomeDir();

    File getShellContextDir();

    File getUserHomeDir();

    File getUserContextDir();

    void customize(Shell shell) throws Exception;
}