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


package org.sonatype.gshell.command;

import org.sonatype.gshell.io.Closer;
import org.sonatype.gshell.util.PrintBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * {@link CommandHelpLoader} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.4
 */
public class CommandHelpLoaderImpl
    implements CommandHelpLoader
{
    public String load(final CommandAction command) throws MissingHelpException, IOException {
        assert command != null;

        Class type = command.getClass();

        // FIXME: Make this i18n friendly, if exists, load Locale version, default this below
        URL resource = type.getResource(type.getSimpleName() + ".help");

        if (resource == null) {
            throw new MissingHelpException(type);
        }

        BufferedReader input = new BufferedReader(new InputStreamReader(resource.openStream()));
        PrintBuffer buff;
        try {
            buff = new PrintBuffer();
            String line;

            while ((line = input.readLine()) != null) {
                if (!line.startsWith("#")) {
                    buff.println(line);
                }
            }
        }
        finally {
            Closer.close(input);
        }

        return buff.toString();
    }
}