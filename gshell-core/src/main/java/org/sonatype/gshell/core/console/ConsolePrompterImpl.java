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

package org.sonatype.gshell.core.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.Branding;
import org.sonatype.gshell.VariableNames;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.util.ansi.AnsiRenderer;
import org.sonatype.gshell.console.Console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link org.sonatype.gshell.console.Console.Prompter} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ConsolePrompterImpl
    implements Console.Prompter, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String SHELL_BRANDING = "shell.branding";

    // NOTE: Have to use %{} here to avoid causing problems with ${} variable interpolation

    private static final Pattern PATTERN = Pattern.compile("\\%\\{([^}]+)\\}");

    private final AnsiRenderer renderer = new AnsiRenderer();

    private final Variables vars;

    private final Branding branding;

    public ConsolePrompterImpl(final Variables vars, final Branding branding) {
        assert vars != null;
        assert branding != null;
        this.vars = vars;
        this.branding = branding;
    }

    public String prompt() {
        String pattern = vars.get(SHELL_PROMPT, String.class);
        String prompt = interpolate(pattern);

        // Use a default prompt if we don't have anything here
        if (prompt == null) {
            prompt = interpolate(branding.getPrompt());
            if (prompt == null) {
                prompt = DEFAULT_PROMPT;
            }
        }

        //
        // TODO: Support rendering ~ for home dir here somewhere
        //

        // Encode ANSI muck if it looks like there are codes encoded, need to render here as the console uses raw streams
        if (AnsiRenderer.test(prompt)) {
            prompt = renderer.render(prompt);
        }

        return prompt;
    }

    private String evaluate(String input) {
        Matcher matcher = PATTERN.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1);
            Object rep = vars.get(key);
            if (rep == null) {
                rep = System.getProperty(key);
            }
            if (rep != null) {
                input = input.replace(matcher.group(0), rep.toString());
                matcher.reset(input);
            }
        }

        return input;
    }

    private String interpolate(final String pattern) {
        String prompt = null;
        if (pattern != null) {
            prompt = evaluate(pattern);
        }
        return prompt;
    }
}