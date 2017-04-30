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
package com.planet57.gshell.shell;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.util.ReplacementParser;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.fusesource.jansi.AnsiRenderer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.planet57.gshell.variables.VariableNames.SHELL_PROMPT;
import static com.planet57.gshell.variables.VariableNames.SHELL_RPROMPT;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_HOME;

/**
 * Shell prompt, which determines the prompt from the {@link VariableNames#SHELL_PROMPT} expression.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellPrompt
{
  // FIXME: could refactor to remove needing fields for Variables/Branding

  private final Variables variables;

  private final Branding branding;

  private final ReplacementParser parser;

  public ShellPrompt(final Variables variables, final Branding branding) {
    this.variables = checkNotNull(variables);
    this.branding = checkNotNull(branding);

    parser = new ReplacementParser()
    {
      @Override
      protected Object replace(@Nonnull final String key) {
        // HACK: Handled some magic with shell.user.dir~ (only if shell.user.dir exists)
        if (key.equals(SHELL_USER_DIR + "~") && variables.contains(SHELL_USER_DIR)) {
          String home = variables.require(SHELL_USER_HOME, File.class).getAbsolutePath();
          String current = variables.require(SHELL_USER_DIR, File.class).getAbsolutePath();
          if (current.startsWith(home)) {
            return "~" + current.substring(home.length(), current.length());
          }
          else {
            return current;
          }
        }

        // HACK: Handled some magic with shell.user.dir~. (only if shell.user.dir exists). THis is similar to bash \w
        if (key.equals(SHELL_USER_DIR + "~.") && variables.contains(SHELL_USER_DIR)) {
          String home = variables.require(SHELL_USER_HOME, File.class).getAbsolutePath();
          File current = variables.require(SHELL_USER_DIR, File.class).getAbsoluteFile();
          if (current.getAbsolutePath().equals(home)) {
            return "~";
          }
          else {
            return current.getName();
          }
        }

        Object rep = variables.get(key);
        if (rep == null) {
          rep = System.getProperty(key);
        }
        return rep;
      }
    };
  }

  public String prompt() {
    String pattern = variables.get(SHELL_PROMPT, String.class);
    String prompt = evaluate(pattern);

    if (prompt == null) {
      prompt = evaluate(branding.getPrompt());
    }

    if (AnsiRenderer.test(prompt)) {
      prompt = AnsiRenderer.render(prompt);
    }

    return prompt;
  }

  /**
   * @since 3.0
   */
  @Nullable
  public String rprompt() {
    String pattern = variables.get(SHELL_RPROMPT, String.class);
    String prompt = evaluate(pattern);

    if (prompt == null) {
      prompt = evaluate(branding.getRightPrompt());
    }

    if (prompt != null) {
      if (AnsiRenderer.test(prompt)) {
        prompt = AnsiRenderer.render(prompt);
      }
    }

    return prompt;
  }

  @Nullable
  private String evaluate(@Nullable final String expression) {
    String prompt = null;
    if (expression != null) {
      prompt = parser.parse(expression);
    }
    return prompt;
  }
}
