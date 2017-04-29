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
package com.planet57.gshell.help;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import com.planet57.gshell.command.CommandHelper;
import com.planet57.gshell.shell.Shell;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for meta-documentation.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class MetaHelpPage
    implements HelpPage
{
  private final String name;

  private final String resource;

  private final HelpContentLoader loader;

  private final ResourceBundle resources;

  public MetaHelpPage(final String name, final String resource, final HelpContentLoader loader) {
    this.name = checkNotNull(name);
    this.resource = checkNotNull(resource);
    this.loader = checkNotNull(loader);
    this.resources = ResourceBundle.getBundle(resource);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return resources.getString(CommandHelper.COMMAND_DESCRIPTION);
  }

  @Override
  public void render(final Shell shell, final PrintWriter out) throws Exception {
    checkNotNull(shell);
    checkNotNull(out);

    Interpolator interp = new StringSearchInterpolator("@{", "}");
    interp.addValueSource(new PrefixedObjectValueSource("command.", this));
    interp.addValueSource(new PrefixedObjectValueSource("branding.", shell.getBranding()));
    interp.addValueSource(new AbstractValueSource(false)
    {
      @Override
      public Object getValue(final String expression) {
        return shell.getVariables().get(expression);
      }
    });
    interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

    String text = loader.load(resource, Thread.currentThread().getContextClassLoader());
    out.println(interp.interpolate(text));
  }

  @Override
  public String toString() {
    return "MetaHelpPage{" +
      "name='" + name + '\'' +
      ", resource='" + resource + '\'' +
      '}';
  }
}
