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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

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

                for (String type : config.getCommands()) {
                    try {
                        registerCommand(type);
                    }
                    catch (Exception e) {
                        log.error("Failed to register command: " + type, e);
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
}