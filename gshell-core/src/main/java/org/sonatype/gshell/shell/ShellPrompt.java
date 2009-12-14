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

package org.sonatype.gshell.shell;

import org.fusesource.jansi.AnsiRenderer;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.util.ReplacementParser;
import org.sonatype.gshell.vars.VariableNames;
import org.sonatype.gshell.vars.Variables;

/**
 * Shell {@link ConsolePrompt}, which determins the prompt from the {@link org.sonatype.gshell.vars.VariableNames#SHELL_PROMPT} expression.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellPrompt
    implements ConsolePrompt, VariableNames
{
    private final AnsiRenderer renderer = new AnsiRenderer();

    private final Variables vars;

    private final Branding branding;

    private final ReplacementParser parser;

    public ShellPrompt(final Variables vars, final Branding branding) {
        assert vars != null;
        this.vars = vars;
        assert branding != null;
        this.branding = branding;
        this.parser = new ReplacementParser()
        {
            @Override
            protected Object replace(final String key) {
                Object rep = vars.get(key);
                if (rep == null) {
                    rep = System.getProperty(key);
                }
                return rep;
            }
        };
    }

    public String prompt() {
        String pattern = vars.get(SHELL_PROMPT, String.class);
        String prompt = evaluate(pattern);

        // Use a default prompt if we don't have anything here
        if (prompt == null) {
            prompt = evaluate(branding.getPrompt());
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

    private String evaluate(final String expression) {
        String prompt = null;
        if (expression != null) {
            prompt = parser.parse(expression);
        }
        return prompt;
    }
}