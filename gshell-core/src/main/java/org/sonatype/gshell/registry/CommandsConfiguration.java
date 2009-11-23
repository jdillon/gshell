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

package org.sonatype.gshell.registry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Container for command configuration.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandsConfiguration
    implements Comparable<CommandsConfiguration>
{
    private static final String ID = "id";

    private static final String ENABLE = "enable";

    private static final String COMMANDS = "commands";

    private static final String AUTO_REGISTER_PRIORITY = "auto-register-priority";

    private static final String DEFAULT_AUTO_REGISTER_PRIORITY = "50";

    private final URL source;

    private final Properties props = new Properties();

    public CommandsConfiguration(final URL source) throws IOException {
        assert source != null;
        this.source = source;
        props.load(new BufferedInputStream(source.openStream()));
    }

    public String getId() {
        return props.getProperty(ID);
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(props.getProperty(ENABLE, Boolean.TRUE.toString()));
    }

    public String[] getCommands() {
        String tmp = props.getProperty(COMMANDS, "").trim();
        if (tmp.length() == 0) {
            return new String[0];
        }
        return tmp.split(",");
    }

    public int getAutoRegisterPriority() {
        return Integer.parseInt(props.getProperty(AUTO_REGISTER_PRIORITY, DEFAULT_AUTO_REGISTER_PRIORITY));
    }

    public int compareTo(final CommandsConfiguration target) {
        int us = getAutoRegisterPriority();
        int them = target.getAutoRegisterPriority();
        return (us < them ? -1 : (us == them ? 0 : 1));
    }

    @Override
    public String toString() {
        return getId() + " -> " + source.toString();
    }
}