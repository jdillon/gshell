/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.core.impl;

import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Registers commands in order.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=CommandRegistrar.class)
public class CommandRegistrar
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String COMMANDS_PROPERTIES = "META-INF/org.apache.maven.shell/commands.properties";

    @Requirement
    private CommandRegistry commandRegistry;

    public void registerCommands() throws Exception {
        log.debug("Discovering commands configuration");

        List<CommandsConfiguration> configurations = new LinkedList<CommandsConfiguration>();

        Enumeration<URL> resources = ClassLoader.getSystemResources(COMMANDS_PROPERTIES);
        if (resources != null && resources.hasMoreElements()) {
            log.debug("Discovered:");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                log.debug("    {}", url);
                CommandsConfiguration config = new CommandsConfiguration(url);
                configurations.add(config);
            }
        }
        else {
            log.debug("No commands configuration discovered");
            return;
        }

        Collections.sort(configurations);

        for (CommandsConfiguration config : configurations) {
            log.debug("Registering commands for: {}", config);

            for (String name : config.getAutoRegisterCommands()) {
                commandRegistry.registerCommand(name);
            }
        }
    }

    private static class CommandsConfiguration
        implements Comparable<CommandsConfiguration>
    {
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

        @Override
        public int compareTo(final CommandsConfiguration target) {
            int us = getAutoRegisterPriority();
            int them = target.getAutoRegisterPriority();
            return (us<them ? -1 : (us==them ? 0 : 1));
        }

        @Override
        public String toString() {
            return source.toString();
        }
    }
}