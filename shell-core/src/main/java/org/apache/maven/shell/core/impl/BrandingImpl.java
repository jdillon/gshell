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

package org.apache.maven.shell.core.impl;

import org.apache.maven.shell.Branding;
import org.apache.maven.shell.VariableNames;

import java.io.File;
import java.io.IOException;

/**
 * The default {@link Branding} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class BrandingImpl
    implements Branding, VariableNames
{
    private static final String ETC = "etc";

    private static final String USER_HOME = "user.home";

    public String getDisplayName() {
        return "Apache Maven Shell";
    }

    public String getProgramName() {
        return "mvnsh";
    }

    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    public String getAboutMessage() {
        return getDisplayName();
    }

    public String getWelcomeMessage() {
        // io.out.println("@|bold,red Apache Maven| @|bold Shell|");
        // io.out.println(StringUtils.repeat("-", io.getTerminal().getTerminalWidth() - 1));

        return getDisplayName();
    }

    public String getGoodbyeMessage() {
        return "Goodbye!";
    }

    public String getDefaultPrompt() {
        // String.format("@|bold %s|:%%{%s}> ", System.getProperty(MVNSH_PROGRAM), MVNSH_USER_DIR));

        return "mvnsh> ";
    }

    public String getProfileScriptName() {
        return String.format("%s.profile", getProgramName());
    }

    public String getInteractiveScriptName() {
        return String.format("%s.rc", getProgramName());
    }

    public String getHistoryFileName() {
        return String.format("%s.history", getProgramName());
    }

    protected File resolveFile(final File file) {
        assert file != null;
        try {
            return file.getCanonicalFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File resolveFile(final String fileName) {
        assert fileName != null;
        return resolveFile(new File(fileName));
    }

    public File getShellHomeDir() {
        return resolveFile(System.getProperty(MVNSH_HOME));
    }

    public File getShellContextDir() {
        return resolveFile(new File(getShellHomeDir(), ETC));
    }

    public File getUserHomeDir() {
        return resolveFile(System.getProperty(USER_HOME));
    }

    public File getUserContextDir() {
        return resolveFile(new File(getUserHomeDir(), String.format(".%s", getProgramName())));
    }
}