/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.shell;

import javax.inject.Inject;
import javax.inject.Provider;
import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.util.ReplacementParser;
import org.sonatype.gshell.variables.Variables;

import java.io.File;

import static org.sonatype.gshell.variables.VariableNames.*;

/**
 * Shell {@link ConsolePrompt}, which determines the prompt from the
 * {@link org.sonatype.gshell.variables.VariableNames#SHELL_PROMPT} expression.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellPrompt
    implements ConsolePrompt
{
    private final Provider<Variables> variables;

    private final Branding branding;

    private final ReplacementParser parser;

    @Inject
    public ShellPrompt(final Provider<Variables> variables, final Branding branding) {
        assert variables != null;
        this.variables = variables;
        assert branding != null;
        this.branding = branding;

        parser = new ReplacementParser()
        {
            @Override
            protected Object replace(final String key) {
                Variables vars = variables.get();

                // HACK: Handled some magic with shell.user.dir~ (only if shell.user.dir exists)
                if (key.equals(SHELL_USER_DIR + "~") && vars.contains(SHELL_USER_DIR)) {
                    String home = vars.get(SHELL_USER_HOME, File.class).getAbsolutePath();
                    String current = vars.get(SHELL_USER_DIR, File.class).getAbsolutePath();
                    if (current.startsWith(home)) {
                        return "~" + current.substring(home.length(), current.length());
                    }
                    else {
                        return current;
                    }
                }
                
                // HACK: Handled some magic with shell.user.dir~. (only if shell.user.dir exists). THis is similar to bash \w
                if (key.equals(SHELL_USER_DIR + "~.") && vars.contains(SHELL_USER_DIR)) {
                    String home = vars.get(SHELL_USER_HOME, File.class).getAbsolutePath();
                    File current = vars.get(SHELL_USER_DIR, File.class).getAbsoluteFile();
                    if (current.getAbsolutePath().equals(home)) {
                        return "~";
                    }
                    else {
                        return current.getName();
                    }
                }                

                Object rep = vars.get(key);
                if (rep == null) {
                    rep = System.getProperty(key);
                }
                return rep;
            }
        };
    }

    public String prompt() {
        String pattern = variables.get().get(SHELL_PROMPT, String.class);
        String prompt = evaluate(pattern);

        // Use a default prompt if we don't have anything here
        if (prompt == null) {
            prompt = evaluate(branding.getPrompt());
        }

        // Encode ANSI muck if it looks like there are codes encoded, need to render here as the console uses raw streams
        if (AnsiRenderer.test(prompt)) {
            prompt = AnsiRenderer.render(prompt);
        }

        return prompt;
    }

    private String evaluate(final String expression) {
        String prompt = null;
        if (expression != null) {
            prompt = parser.parse(expression);
        }
        return prompt;
    }
}