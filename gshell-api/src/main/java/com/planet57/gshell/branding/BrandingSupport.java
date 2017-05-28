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
import java.net.URI;
import java.util.Properties;

import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.variables.Variables;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.command.Node.CURRENT;
import static com.planet57.gshell.command.Node.PATH_SEPARATOR;
import static com.planet57.gshell.command.Node.ROOT;
import static com.planet57.gshell.variables.VariableNames.SHELL_GROUP;
import static com.planet57.gshell.variables.VariableNames.SHELL_GROUP_PATH;
import static com.planet57.gshell.variables.VariableNames.SHELL_HOME;
import static com.planet57.gshell.variables.VariableNames.SHELL_PROGRAM;
import static com.planet57.gshell.variables.VariableNames.SHELL_PROMPT;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_HOME;
import static com.planet57.gshell.variables.VariableNames.SHELL_VERSION;

/**
 * Support for {@link Branding} implementations.
 *
 * @since 2.0
 */
public class BrandingSupport
    implements Branding
{
  /**
   * Render a terminal (width - 1) line for {@link #getGoodbyeMessage()} or {@link #getGoodbyeMessage()}.
   *
   * @since 3.0
   */
  public static final String LINE_TOKEN = "${LINE}";

  private final Properties props;

  public BrandingSupport(@Nullable final Properties props) {
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

  protected Properties getProperties() {
    return props;
  }

  @Override
  public String getDisplayName() {
    return getProgramName();
  }

  @Override
  public String getProgramName() {
    return getProperties().getProperty(SHELL_PROGRAM);
  }

  @Override
  public String getScriptExtension() {
    return getProgramName();
  }

  @Override
  public String getVersion() {
    return getProperties().getProperty(SHELL_VERSION);
  }

  @Override
  public String getWelcomeMessage() {
    PrintBuffer buff = new PrintBuffer();
    buff.format("%s%n", getDisplayName());
    buff.print(LINE_TOKEN);
    return buff.toString();
  }

  @Override
  public String getGoodbyeMessage() {
    return null;
  }

  @Override
  public String getPrompt() {
    // FIXME: may need to adjust ansi-renderer syntax or pre-render before expanding to avoid needing escapes
    //return String.format("\\@\\{bold %s\\}> ", getProgramName());
    return String.format("%s> ", getProgramName());
  }

  @Override
  @Nullable
  public String getRightPrompt() {
    return null;
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
    return resolveFile(System.getProperty(SHELL_HOME));
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
    checkNotNull(shell);
    Variables variables = shell.getVariables();

    // Setup default variables
    if (!variables.contains(SHELL_HOME)) {
      variables.set(SHELL_HOME, getShellHomeDir(), false);
    }
    if (!variables.contains(SHELL_VERSION)) {
      variables.set(SHELL_VERSION, getVersion(), false);
    }
    if (!variables.contains(SHELL_USER_HOME)) {
      variables.set(SHELL_USER_HOME, getUserHomeDir(), false);
    }
    if (!variables.contains(SHELL_PROMPT)) {
      variables.set(SHELL_PROMPT, getPrompt());
    }
    if (!variables.contains(SHELL_USER_DIR)) {
      variables.set(SHELL_USER_DIR, new File(".").getCanonicalFile());
    }
    if (!variables.contains(SHELL_GROUP)) {
      variables.set(SHELL_GROUP, ROOT);
    }
    if (!variables.contains(SHELL_GROUP_PATH)) {
      variables.set(SHELL_GROUP_PATH, String.format("%s%s%s", CURRENT, PATH_SEPARATOR, ROOT));
    }
  }
}
