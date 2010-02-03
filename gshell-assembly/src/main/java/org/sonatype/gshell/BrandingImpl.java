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

package org.sonatype.gshell;

import org.fusesource.jansi.Ansi;
import org.sonatype.gshell.branding.BrandingSupport;
import org.sonatype.gshell.util.PrintBuffer;

import java.io.File;

/**
 * Branding for <tt>gsh</tt>.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class BrandingImpl
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
   
    /*
    // Figlet font name: Georgia11
    private static final String[] BANNER = {
        "                          ,,                 ,,    ,,",
        "   .g8\"\"\"bgd   .M\"\"\"bgd `7MM               `7MM  `7MM",
        " .dP'     `M  ,MI    \"Y   MM                 MM    MM",
        " dM'       `  `MMb.       MMpMMMb.  .gP\"Ya   MM    MM",
        " MM             `YMMNq.   MM    MM ,M'   Yb  MM    MM",
        " MM.    `7MMF'.     `MM   MM    MM 8M\"\"\"\"\"\"  MM    MM",
        " `Mb.     MM  Mb     dM   MM    MM YM.    ,  MM    MM",
        "   `\"bmmmdPY  P\"Ybmmd\"  .JMML  JMML.`Mbmmd'.JMML..JMML."
    };
    */

    @Override
    public String getWelcomeMessage() {
        PrintBuffer buff = new PrintBuffer();

        for (String line : BANNER) {
            buff.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(line).reset());
        }

        buff.println();
        buff.format("%s (%s)", getDisplayName(), getVersion()).println();
        buff.println();
        buff.println("Type '@|bold help|@' for more information.");
        buff.print(line());
        buff.flush();

        return buff.toString();
    }

    @Override
    public String getDisplayName() {
        return getMessages().format("displayName");
    }

    @Override
    public String getGoodbyeMessage() {
        return getMessages().format("goodbye");
    }

    @Override
    public File getUserContextDir() {
        return resolveFile(new File(getUserHomeDir(), ".gshell"));
    }
}