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
package com.planet57.gshell.branding;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Properties;

import com.google.common.base.Strings;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import jline.TerminalFactory;

import static com.planet57.gshell.command.resolver.Node.CURRENT;
import static com.planet57.gshell.command.resolver.Node.PATH_SEPARATOR;
import static com.planet57.gshell.command.resolver.Node.ROOT;

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

  @Override
  public String getDisplayName() {
    return getProgramName();
  }

  @Override
  public String getProgramName() {
    return getProperties().getProperty(VariableNames.SHELL_PROGRAM);
  }

  @Override
  public String getScriptExtension() {
    return getProgramName();
  }

  @Override
  public String getVersion() {
    return getProperties().getProperty(VariableNames.SHELL_VERSION);
  }

  protected String line() {
    return Strings.repeat("-", TerminalFactory.get().getWidth() - 1);
  }

  @Override
  public String getWelcomeMessage() {
    StringWriter buff = new StringWriter();
    PrintWriter out = new PrintWriter(buff);

    out.println(getDisplayName());
    out.print(line());
    out.flush();

    return buff.toString();
  }

  @Override
  public String getGoodbyeMessage() {
    return null;
  }

  @Override
  public String getPrompt() {
    return String.format("@|bold %s|@> ", getProgramName());
  }

  @Override
  public String getProfileScriptName() {
    return String.format("%s.profile", getProgramName());
  }

  @Override
  public String getInteractiveScriptName() {
    return String.format("%s.rc", getProgramName());
  }

  @Override
  public String getHistoryFileName() {
    return String.format("%s.history", getProgramName());
  }

  @Override
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

  @Override
  public File getShellHomeDir() {
    return resolveFile(System.getProperty(VariableNames.SHELL_HOME));
  }

  @Override
  public File getShellContextDir() {
    return resolveFile(new File(getShellHomeDir(), "etc"));
  }

  @Override
  public File getUserHomeDir() {
    return resolveFile(getProperties().getProperty("user.home"));
  }

  @Override
  public File getUserContextDir() {
    return resolveFile(new File(getUserHomeDir(), String.format(".%s", getProgramName())));
  }

  @Override
  public License getLicense() {
    return new LicenseSupport("unknown", (URI) null);
  }

  @Override
  public void customize(final Shell shell) throws Exception {
    assert shell != null;

    Variables vars = shell.getVariables();

    // Setup default variables
    if (!vars.contains(VariableNames.SHELL_HOME)) {
      vars.set(VariableNames.SHELL_HOME, getShellHomeDir(), false);
    }
    if (!vars.contains(VariableNames.SHELL_VERSION)) {
      vars.set(VariableNames.SHELL_VERSION, getVersion(), false);
    }
    if (!vars.contains(VariableNames.SHELL_USER_HOME)) {
      vars.set(VariableNames.SHELL_USER_HOME, getUserHomeDir(), false);
    }
    if (!vars.contains(VariableNames.SHELL_PROMPT)) {
      vars.set(VariableNames.SHELL_PROMPT, getPrompt());
    }
    if (!vars.contains(VariableNames.SHELL_USER_DIR)) {
      vars.set(VariableNames.SHELL_USER_DIR, new File(".").getCanonicalFile());
    }
    if (!vars.contains(VariableNames.SHELL_GROUP)) {
      vars.set(VariableNames.SHELL_GROUP, ROOT);
    }
    if (!vars.contains(VariableNames.SHELL_GROUP_PATH)) {
      vars.set(VariableNames.SHELL_GROUP_PATH, String.format("%s%s%s", CURRENT, PATH_SEPARATOR, ROOT));
    }
  }
}
