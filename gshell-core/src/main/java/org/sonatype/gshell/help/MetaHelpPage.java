/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.help;

import org.sonatype.gshell.command.CommandHelpSupport;
import org.sonatype.gshell.command.descriptor.HelpPageDescriptor;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.ReplacementParser;
import org.sonatype.gshell.vars.Variables;

import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * {@link HelpPage} for meta-documentation.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class MetaHelpPage
    implements HelpPage
{
    private final HelpPageDescriptor desc;

    private final HelpContentLoader loader;

    public MetaHelpPage(final HelpPageDescriptor desc, final HelpContentLoader loader) {
        assert desc != null;
        this.desc = desc;
        assert loader != null;
        this.loader = loader;
    }

    public String getName() {
        return desc.getName();
    }

    private ResourceBundle resources;

    public String getBriefDescription() {
        if (resources == null) {
            resources = ResourceBundle.getBundle(desc.getResource());
        }

        return resources.getString(CommandHelpSupport.COMMAND_DESCRIPTION);
    }

    public void render(final PrintWriter out) {
        assert out != null;

        String text;
        try {
            text = loader.load(desc.getResource(), Thread.currentThread().getContextClassLoader());
            text = evaluate(text);
            out.println(text);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String evaluate(final String input) {
        if (input.contains("@{")) {
            ReplacementParser parser = new ReplacementParser("\\@\\{([^}]+)\\}")
            {
                //
                // TODO: Expose the branding here too
                //
                
                @Override
                protected Object replace(final String key) throws Exception {
                    Object rep = null;
                    if (key.equals(CommandHelpSupport.COMMAND_NAME)) {
                        rep = getName();
                    }
                    else if (key.equals(CommandHelpSupport.COMMAND_DESCRIPTION)) {
                        rep = getBriefDescription();
                    }

                    if (rep == null) {
                        Variables vars = ShellHolder.get().getVariables();
                        rep = vars.get(key);
                    }
                    if (rep == null) {
                        rep = System.getProperty(key);
                    }

                    return rep;
                }
            };

            return parser.parse(input);
        }

        return input;
    }
}