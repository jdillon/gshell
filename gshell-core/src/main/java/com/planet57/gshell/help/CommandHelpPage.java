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

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.Node;
import com.planet57.gshell.command.CommandHelper;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import com.planet57.gshell.util.pref.PreferenceDescriptor;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.fusesource.jansi.AnsiRenderer;
import org.jline.terminal.Terminal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for a command.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class CommandHelpPage
    implements HelpPage
{
  private final Node node;

  private final HelpContentLoader loader;

  private final CommandAction command;

  public CommandHelpPage(final Node node, final HelpContentLoader loader) {
    this.node = checkNotNull(node);
    checkArgument(!node.isGroup());
    this.loader = checkNotNull(loader);
    this.command = node.getAction();
  }

  @Override
  public String getName() {
    return node.getAction().getSimpleName();
  }

  @Override
  public String getDescription() {
    return CommandHelper.getDescription(node.getAction());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  // Public so that ObjectBasedValueSource can access (it really should set accessible so this is not needed)
  @SuppressWarnings("unused")
  public class Helper
  {
    private final CliProcessor clp;

    private final HelpPrinter printer;

    private final PreferenceProcessor pp;

    private final MessageSource messages;

    public Helper(final Terminal terminal, final Branding branding) {
      CommandHelper help = new CommandHelper();
      clp = help.createCliProcessor(command);
      printer = new HelpPrinter(clp, terminal);

      pp = new PreferenceProcessor();
      pp.setBasePath(branding.getPreferencesBasePath());
      pp.addBean(command);

      messages = new ResourceBundleMessageSource(getClass());
    }

    public String getName() {
      return command.getName();
    }

    public String getAbsoluteName() {
      return Node.ROOT + command.getName();
    }

    public String getSimpleName() {
      return command.getSimpleName();
    }

    public String getDescription() {
      return CommandHelper.getDescription(command);
    }

    private void printHeader(final PrintBuffer buff, final String name) {
      buff.format("@|bold %s|@", messages.format(name)).println();
      buff.println();
    }

    public String getArguments() {
      if (clp.getArgumentDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, "section.arguments");
      printer.printArguments(buff, clp.getArgumentDescriptors());

      return buff.toString();
    }

    public String getOptions() {
      if (clp.getOptionDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, "section.options");
      printer.printOptions(buff, clp.getOptionDescriptors());

      return buff.toString();
    }

    public String getPreferences() {
      if (pp.getDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, "section.preferences");

      for (PreferenceDescriptor pd : pp.getDescriptors()) {
        String text = String.format("    %s @|bold %s|@ (%s)",
            pd.getPreferences().absolutePath(), pd.getId(), pd.getSetter().getType().getSimpleName());
        buff.println(AnsiRenderer.render(text));
      }

      return buff.toString();
    }

    public String getDetails() {
      // This ugly muck adds a newline as needed if the last section was not empty
      // and the current section is not empty, so that the page looks correct.

      PrintBuffer buff = new PrintBuffer();
      String content, last;

      content = getOptions();
      buff.append(content);
      last = content;

      content = getArguments();
      if (content.length() != 0 && last.length() != 0) {
        buff.println();
      }
      buff.append(content);
      last = content;

      content = getPreferences();
      if (content.length() != 0 && last.length() != 0) {
        buff.println();
      }
      buff.append(content);

      // newline is already in the help stream

      return buff.toString();
    }
  }

  @Override
  public void render(final Shell shell, final PrintWriter out) throws Exception {
    checkNotNull(shell);
    checkNotNull(out);

    Interpolator interp = new StringSearchInterpolator("@{", "}");
    interp.addValueSource(new PrefixedObjectValueSource("command.", new Helper(shell.getIo().terminal, shell.getBranding())));
    interp.addValueSource(new PrefixedObjectValueSource("branding.", shell.getBranding()));
    interp.addValueSource(new AbstractValueSource(false)
    {
      @Override
      public Object getValue(final String expression) {
        return shell.getVariables().get(expression);
      }
    });
    interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

    String text = loader.load(command.getClass().getName(), command.getClass().getClassLoader());
    out.println(interp.interpolate(text));
  }
}
