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
package com.planet57.gshell;

import java.io.File;

import com.planet57.gshell.branding.Asl2License;
import com.planet57.gshell.branding.BrandingSupport;
import com.planet57.gshell.branding.License;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.variables.Variables;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import static com.planet57.gshell.variables.VariableNames.SHELL_GROUP;
import static com.planet57.gshell.variables.VariableNames.SHELL_USER_DIR;

/**
 * Branding for <tt>gsh</tt>.
 *
 * @since 2.0
 */
public class  BrandingImpl
    extends BrandingSupport
{
  // Figlet font name: ???
  private static final String[] BANNER = {
      "   ____ ____  _          _ _ ",
      "  / ___/ ___|| |__   ___| | |",
      " | |  _\\___ \\| '_ \\ / _ \\ | |",
      " | |_| |___) | | | |  __/ | |",
      "  \\____|____/|_| |_|\\___|_|_|",
      };

  @Override
  public String getWelcomeMessage() {
    PrintBuffer buff = new PrintBuffer();
    for (String line : BANNER) {
      buff.format("@{fg:cyan %s}%n", line);
    }
    buff.println();
    buff.format("%s (%s)%n%n", getDisplayName(), getVersion());
    buff.println("Type '@{bold help}' for more information.");
    buff.format("@{faint %s}", LINE_TOKEN);
    return buff.toString();
  }

  @Override
  public String getDisplayName() {
    return "@{fg:cyan GShell}";
  }

  @Override
  public String getGoodbyeMessage() {
    return "@{fg:green Goodbye!}";
  }

  @Override
  public File getUserContextDir() {
    return resolveFile(new File(getUserHomeDir(), ".gshell"));
  }

  @Override
  public String getPrompt() {
    // FIXME: may need to adjust ansi-renderer syntax or pre-render before expanding to avoid needing escapes
    return String.format("\\@\\|bold %s\\|\\@\\(${%s}\\):${%s}> ", getProgramName(), SHELL_GROUP, SHELL_USER_DIR);
  }

//  @Nullable
//  @Override
//  public String getRightPrompt() {
//    // FIXME: may need to adjust ansi-renderer syntax or pre-render before expanding to avoid needing escapes
//    return "\\@\\|intensity_faint $(date)\\|\\@";
//  }

  @Override
  public License getLicense() {
    return new Asl2License();
  }

  @Override
  public void customize(final Shell shell) throws Exception {
    super.customize(shell);

    Terminal terminal = shell.getTerminal();
    Variables variables = shell.getVariables();

    // HACK: testing adjustment to highlighter colors; pending letting users configure this via profile/rc
    int maxColors = terminal.getNumericCapability(InfoCmp.Capability.max_colors);
    if (maxColors >= 256) {
      variables.set("HIGHLIGHTER_COLORS", "rs=35:st=32:nu=32:co=32:va=36:vn=36:fu=1;38;5;69:bf=1;38;5;197:re=90");
    }
    else {
      variables.set("HIGHLIGHTER_COLORS", "rs=35:st=32:nu=32:co=32:va=36:vn=36:fu=94:bf=91:re=90");
    }
  }
}
