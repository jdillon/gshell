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

package org.apache.gshell.core.console;

import org.apache.gshell.Branding;
import org.apache.gshell.VariableNames;
import org.apache.gshell.Variables;
import org.apache.gshell.ansi.AnsiRenderer;
import org.apache.gshell.console.Console;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link org.apache.gshell.console.Console.Prompter} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class ConsolePrompterImpl
    implements Console.Prompter, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String SHELL_BRANDING = "shell.branding";

    // NOTE: Have to use %{} here to avoid causing problems with ${} variable interpolation

    private final Interpolator interp = new StringSearchInterpolator("%{", "}");

    private final AnsiRenderer renderer = new AnsiRenderer();

    private final Variables vars;

    private final Branding branding;

    public ConsolePrompterImpl(final Variables vars, final Branding branding) {
        assert vars != null;
        assert branding != null;
        this.vars = vars;
        this.branding = branding;

        interp.addValueSource(new AbstractValueSource(false) {
            public Object getValue(final String expression) {
                return vars.get(expression);
            }
        });
        interp.addValueSource(new PrefixedObjectValueSource(SHELL_BRANDING, branding));
        interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
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

        // Encode ANSI muck if it looks like there are codes encoded
        if (AnsiRenderer.test(prompt)) {
            prompt = renderer.render(prompt);
        }

        return prompt;
    }

    private String interpolate(final String pattern) {
        String prompt = null;
        if (pattern != null) {
            try {
                prompt = interp.interpolate(pattern);
            }
            catch (InterpolationException e) {
                log.warn("Failed to interpolate: " + pattern, e);
            }
        }
        return prompt;
    }
}