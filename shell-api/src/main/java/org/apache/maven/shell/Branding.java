/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell;

import java.io.File;
import java.io.IOException;

/**
 * Defines the basic elements for branding a shell.
 *
 * @version $Rev: 574424 $ $Date: 2007-09-11 08:47:18 +0700 (Tue, 11 Sep 2007) $
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

    // shell home, where the lib/ & bin/ are
    File getShellHomeDir();

    // shell shared dir, ~/<shell_home>/etc
    File getShellContextDir();

    // user home, ~/
    File getUserHomeDir();

    // user state, ~/.foo
    File getUserContextDir();

    /*
    String getPropertyName(String name);

    String getProperty(String name);

    String getProperty(String name, String defaultValue);
    */
}