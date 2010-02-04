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

package org.sonatype.gshell.branding;

import jline.TerminalFactory;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;
import org.sonatype.gshell.vars.Variables;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import static org.sonatype.gshell.vars.VariableNames.SHELL_HOME;
import static org.sonatype.gshell.vars.VariableNames.SHELL_PROGRAM;
import static org.sonatype.gshell.vars.VariableNames.SHELL_PROMPT;
import static org.sonatype.gshell.vars.VariableNames.SHELL_USER_DIR;
import static org.sonatype.gshell.vars.VariableNames.SHELL_USER_HOME;
import static org.sonatype.gshell.vars.VariableNames.SHELL_VERSION;

/**
 * Support for {@link Branding} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class BrandingSupport
    implements Branding
{
    private final MessageSource messages = new ResourceBundleMessageSource()
        .add(false, getClass());

    private final Properties props;

    public BrandingSupport(final Properties props) {
        if (props == null) {
            this.props = System.getProperties();
        }
        else {
            this.props = props;
        }
    }

    public BrandingSupport() {
        this(null);
    }

    protected MessageSource getMessages() {
        return messages;
    }

    protected Properties getProperties() {
        return props;
    }

    public String getDisplayName() {
        return getProgramName();
    }

    public String getProgramName() {
        return getProperties().getProperty(SHELL_PROGRAM);
    }

    public String getScriptExtension() {
        return getProgramName();
    }

    public String getVersion() {
        return getProperties().getProperty(SHELL_VERSION);
    }

    protected String line() {
        return Strings.repeat("-", TerminalFactory.get().getWidth() - 1);
    }

    public String getWelcomeMessage() {
        StringWriter buff = new StringWriter();
        PrintWriter out = new PrintWriter(buff);

        out.println(getDisplayName());
        out.print(line());
        out.flush();

        return buff.toString();
    }

    public String getGoodbyeMessage() {
        return null;
    }

    public String getPrompt() {
        return String.format("@|bold %s|@> ", getProgramName());
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

    public String getPreferencesBasePath() {
        return getProgramName();
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
        return resolveFile(getProperties().getProperty("user.home"));
    }

    public File getUserContextDir() {
        return resolveFile(new File(getUserHomeDir(), String.format(".%s", getProgramName())));
    }

    public License getLicense() {
        return new LicenseSupport(null, (URL)null);
    }

    public void customize(final Shell shell) throws Exception {
        assert shell != null;

        Variables vars = shell.getVariables();

        // Setup default variables
        if (!vars.contains(SHELL_HOME)) {
            vars.set(SHELL_HOME, getShellHomeDir(), false);
        }
        if (!vars.contains(SHELL_VERSION)) {
            vars.set(SHELL_VERSION, getVersion(), false);
        }
        if (!vars.contains(SHELL_USER_HOME)) {
            vars.set(SHELL_USER_HOME, getUserHomeDir(), false);
        }
        if (!vars.contains(SHELL_PROMPT)) {
            vars.set(SHELL_PROMPT, getPrompt());
        }
        if (!vars.contains(SHELL_USER_DIR)) {
            vars.set(SHELL_USER_DIR, new File(".").getCanonicalFile());
        }
    }
}