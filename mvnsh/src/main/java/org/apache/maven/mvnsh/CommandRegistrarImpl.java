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

package org.apache.maven.mvnsh;

import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.registry.CommandRegistry;
import org.apache.maven.shell.registry.CommandRegistrar;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Default implementation of the {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role= CommandRegistrar.class)
public class CommandRegistrarImpl
    implements CommandRegistrar
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private PlexusContainer container;

    @Requirement
    private CommandRegistry registry;

    public CommandRegistrarImpl() {}
    
    public CommandRegistrarImpl(final PlexusContainer container, final CommandRegistry registry) {
        assert container != null;
        assert registry != null;
        this.container = container;
        this.registry = registry;
    }

    public void registerCommands() throws Exception {
        List<CommandsConfiguration> configurations = discoverConfigurations();

        if (!configurations.isEmpty()) {
            Collections.sort(configurations);

            for (CommandsConfiguration config : configurations) {
                log.debug("Registering commands for: {}", config);

                for (String name : config.getAutoRegisterCommands()) {
                    Command command = createCommand(name);
                    registry.registerCommand(name, command);
                }
            }
        }
    }

    protected Command createCommand(final String name) throws Exception{
        assert name != null;
        return container.lookup(Command.class, name);
    }

    private List<CommandsConfiguration> discoverConfigurations() throws IOException {
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

    private static class CommandsConfiguration
        implements Comparable<CommandsConfiguration>
    {
        private static final String ID = "id";
        
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
            return getId() + " -> " + source.toString();
        }
    }
}