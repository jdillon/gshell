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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.CommandDocumenter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Support for {@link CommandRegistrar} implementations
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class CommandRegistrarSupport
    implements CommandRegistrar
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private List<CommandsConfiguration> configurations = new LinkedList<CommandsConfiguration>();

    public void registerCommands() throws Exception {
        List<CommandsConfiguration> configurations = discoverConfigurations();

        if (!configurations.isEmpty()) {
            Collections.sort(configurations);

            for (CommandsConfiguration config : configurations) {
                if (!config.isEnabled()) {
                    log.debug("Skipping disabled commands: {}", config);
                    continue;
                }

                log.debug("Registering commands for: {}", config);

                this.configurations.add(config);

                for (String name : config.getAutoRegisterCommands()) {
                    String type = config.getCommandType(name);
                    if (type == null) {
                        log.error("Missing class type for command: " + name);
                        continue;
                    }

                    try {
                        registerCommand(name, type);
                    }
                    catch (Exception e) {
                        log.error("Failed to register command: " + name, e);
                    }
                }
            }
        }
    }

    protected List<CommandsConfiguration> discoverConfigurations() throws IOException {
        log.debug("Discovering commands configuration");

        List<CommandsConfiguration> list = new LinkedList<CommandsConfiguration>();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Enumeration<URL> resources = cl.getResources(COMMANDS_PROPERTIES);
        if (resources != null && resources.hasMoreElements()) {
            log.debug("Discovered:");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                log.debug("    {}", url);
                CommandsConfiguration config = new CommandsConfiguration(url);
                list.add(config);
            }
        }

        return list;
    }

    public static class CommandsConfiguration
        implements Comparable<CommandsConfiguration>
    {
        private static final String ID = "id";

        private static final String ENABLE = "enable";

        private static final String AUTO_REGISTER_PRIORITY = "auto-register-priority";

        private static final String DEFAULT_AUTO_REGISTER_PRIORITY = "50";

        private static final String AUTO_REGISTER_COMMANDS = "auto-register-commands";

        private final URL source;

        private final Properties props = new Properties();

        private CommandsConfiguration(final URL source) throws IOException {
            assert source != null;
            this.source = source;
            props.load(new BufferedInputStream(source.openStream()));
        }

        public String getId() {
            return props.getProperty(ID);
        }

        public boolean isEnabled() {
            if (props.containsKey(ENABLE)) {
                return Boolean.parseBoolean(props.getProperty(ENABLE));
            }
            return true;
        }

        public int getAutoRegisterPriority() {
            return Integer.parseInt(props.getProperty(AUTO_REGISTER_PRIORITY, DEFAULT_AUTO_REGISTER_PRIORITY));
        }

        public String[] getAutoRegisterCommands() {
            String tmp = props.getProperty(AUTO_REGISTER_COMMANDS);
            if (tmp == null) {
                return new String[0];
            }

            return tmp.split(",");
        }

        public String getCommandType(final String name) {
            return props.getProperty(CommandDocumenter.COMMAND_DOT + name);
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
}