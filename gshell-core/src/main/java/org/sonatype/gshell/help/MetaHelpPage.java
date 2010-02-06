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

import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.sonatype.gshell.command.support.CommandHelpSupport;
import org.sonatype.gshell.command.descriptor.HelpPageDescriptor;
import org.sonatype.gshell.shell.ShellHolder;

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

    public String getDescription() {
        if (resources == null) {
            resources = ResourceBundle.getBundle(desc.getResource());
        }

        return resources.getString(CommandHelpSupport.COMMAND_DESCRIPTION);
    }

    public void render(final PrintWriter out) {
        assert out != null;

        Interpolator interp = new StringSearchInterpolator("@{", "}");
        interp.addValueSource(new PrefixedObjectValueSource("command.", this));
        interp.addValueSource(new PrefixedObjectValueSource("branding.", ShellHolder.get().getBranding()));
        interp.addValueSource(new AbstractValueSource(false)
        {
            public Object getValue(final String expression) {
                return ShellHolder.get().getVariables().get(expression);
            }
        });
        interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        
        try {
            String text = loader.load(desc.getResource(), Thread.currentThread().getContextClassLoader());
            out.println(interp.interpolate(text));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}