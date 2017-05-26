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

import com.google.common.base.CharMatcher;
import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.Node;
import com.planet57.gshell.command.CommandHelper;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.pref.PreferenceDescriptor;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.variables.Variables;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.jline.terminal.Terminal;
import com.planet57.gshell.util.i18n.I18N;
import com.planet57.gshell.util.i18n.MessageBundle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HelpPage} for a command.
 *
 * @since 2.5
 */
public class CommandHelpPage
    implements HelpPage
{
  private interface Messages
    extends MessageBundle
  {
    @DefaultMessage("ARGUMENTS")
    String arguments();

    @DefaultMessage("OPTIONS")
    String options();

    @DefaultMessage("PREFERENCES")
    String preferences();
  }

  private static final Messages messages = I18N.create(Messages.class);

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
    return node.getAction().getDescription();
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

    public Helper(final Branding branding, final int maxWidth) {
      CommandHelper help = new CommandHelper();
      clp = help.createCliProcessor(command);
      printer = new HelpPrinter(clp, maxWidth);

      pp = new PreferenceProcessor();
      pp.setBasePath(branding.getPreferencesBasePath());
      pp.addBean(command);
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
      return command.getDescription();
    }

    private void printHeader(final PrintBuffer buff, final String name) {
      buff.format("@|bold %s|@%n", name);
      buff.println();
    }

    public String getArguments() {
      if (clp.getArgumentDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, CommandHelpPage.messages.arguments());
      printer.printDescriptors(buff, clp.getArgumentDescriptors());

      return buff.toString();
    }

    public String getOptions() {
      if (clp.getOptionDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, CommandHelpPage.messages.options());
      printer.printDescriptors(buff, clp.getOptionDescriptors());

      return buff.toString();
    }

    public String getPreferences() {
      if (pp.getDescriptors().isEmpty()) {
        return "";
      }

      PrintBuffer buff = new PrintBuffer();
      printHeader(buff, CommandHelpPage.messages.preferences());

      for (PreferenceDescriptor pd : pp.getDescriptors()) {
        String text = String.format("    %s @|bold %s|@ (%s)",
            pd.getPreferences().absolutePath(), pd.getId(), pd.getSetter().getType().getSimpleName());
        buff.println(text);
      }

      return buff.toString();
    }

    public String getDetails() {
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

      // strip off any trailing white-space
      return CharMatcher.whitespace().trimTrailingFrom(buff.toString());
    }
  }

  @Override
  public void render(final Shell shell, final PrintWriter out) throws Exception {
    checkNotNull(shell);
    checkNotNull(out);

    final Branding branding = shell.getBranding();
    final Terminal terminal = shell.getTerminal();
    final Variables variables = shell.getVariables();

    Interpolator interp = new StringSearchInterpolator("@{", "}");
    interp.addValueSource(new PrefixedObjectValueSource("command.", new Helper(branding, terminal.getWidth())));
    interp.addValueSource(new PrefixedObjectValueSource("branding.", branding));
    interp.addValueSource(new AbstractValueSource(false)
    {
      @Override
      public Object getValue(final String expression) {
        return variables.get(expression);
      }
    });
    interp.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

    String text = loader.load(command.getClass().getName(), command.getClass().getClassLoader());
    out.println(interp.interpolate(text));
  }
}
