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
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * The default {@link Branding} component.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class BrandingImpl
    implements Branding, VariableNames
{
    public String getDisplayName() {
        return "@|bold,red Apache Maven| @|bold Shell|";
    }

    public String getProgramName() {
        return System.getProperty(SHELL_PROGRAM);
    }

    @Override
    public String getScriptExtension() {
        return "mvnsh";
    }

    public String getVersion() {
        return System.getProperty(SHELL_VERSION);
    }

    public String getAboutMessage() {
        return getDisplayName();
    }

    protected String line() {
        return StringUtils.repeat("-", jline.Terminal.getTerminal().getTerminalWidth() - 1);
    }

    public String getWelcomeMessage() {
        StringWriter buff = new StringWriter();
        PrintWriter out = new PrintWriter(buff);

        out.println(getDisplayName());
        out.println(line());
        out.flush();

        return buff.toString();
    }

    public String getGoodbyeMessage() {
        return "Goodbye!"; // TODO: i18n
    }

    public String getPrompt() {
        return String.format("@|bold %s|:%%{%s}> ", getProgramName(), SHELL_USER_DIR);
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
        return resolveFile(System.getProperty(SHELL_HOME));
    }

    public File getShellContextDir() {
        return resolveFile(new File(getShellHomeDir(), "etc"));
    }

    public File getUserHomeDir() {
        return resolveFile(System.getProperty("user.home"));
    }

    public File getUserContextDir() {
        return resolveFile(new File(getUserHomeDir(), String.format(".%s", getProgramName())));
    }
}