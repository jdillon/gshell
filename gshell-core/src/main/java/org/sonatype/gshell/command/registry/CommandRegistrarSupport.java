/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.command.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.descriptor.CommandDescriptor;
import org.sonatype.gshell.command.descriptor.CommandSetDescriptor;
import org.sonatype.gshell.command.descriptor.CommandsDescriptor;
import org.sonatype.gshell.command.descriptor.DiscoveredCommandDescriptorEvent;
import org.sonatype.gshell.command.descriptor.DiscoveredCommandSetDescriptorEvent;
import org.sonatype.gshell.command.descriptor.DiscoveredCommandsDescriptorEvent;
import org.sonatype.gshell.command.descriptor.io.xpp3.CommandsXpp3Reader;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.util.io.Closer;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Support for {@link CommandRegistrar} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public abstract class CommandRegistrarSupport
    implements CommandRegistrar
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final EventManager events;

    private String[] descriptorSearchPath = { DEFAULT_DESCRIPTOR_LOCATION };

    private final List<CommandSetDescriptor> descriptors = new LinkedList<CommandSetDescriptor>();

    protected CommandRegistrarSupport(final EventManager events) {
        assert events != null;
        this.events = events;
    }

    public String[] getDescriptorSearchPath() {
        return descriptorSearchPath;
    }

    public void setDescriptorSearchPath(final String... path) {
        assert path != null;
        this.descriptorSearchPath = path;
    }

    public List<CommandSetDescriptor> getDescriptors() {
        return descriptors;
    }

    public void registerCommands() throws Exception {
        List<CommandsDescriptor> descriptors = discoverDescriptors();
        List<CommandSetDescriptor> commandSets = new ArrayList<CommandSetDescriptor>();
        for (CommandsDescriptor config : descriptors) {
            events.publish(new DiscoveredCommandsDescriptorEvent(config));

            commandSets.addAll(config.getCommandSets());
        }

        if (!commandSets.isEmpty()) {
            Collections.sort(commandSets);

            for (CommandSetDescriptor config : commandSets) {
                events.publish(new DiscoveredCommandSetDescriptorEvent(config));

                this.descriptors.add(config);

                if (!config.isEnabled()) {
                    log.debug("Skipping disabled commands: {}", config);
                    continue;
                }

                registerCommandSet(config);
            }
        }
    }

    protected void registerCommandSet(final CommandSetDescriptor config) {
        assert config != null;

        log.debug("Registering commands for: {}", config);

        for (CommandDescriptor command : config.getCommands()) {
            command.createCommandSetDescriptorAssociation(config);
            events.publish(new DiscoveredCommandDescriptorEvent(command));

            if (command.isEnabled()) {
                registerCommand(command);
            }
            else {
                log.debug("Skipping disabled command: {}", command);
            }
        }
    }

    protected void registerCommand(final CommandDescriptor config) {
        assert config != null;

        log.debug("Registering command for: {}", config);

        String type = config.getAction();
        String name = config.getName();

        try {
            if (name == null) {
                registerCommand(type);
            }
            else {
                registerCommand(name, type);
            }
        }
        catch (Throwable e) {
            log.error("Failed to register command: " + type, e);
        }
    }

    protected List<CommandsDescriptor> discoverDescriptors() throws Exception {
        List<CommandsDescriptor> list = new LinkedList<CommandsDescriptor>();

        for (String location : getDescriptorSearchPath()) {
            log.debug("Discovering commands descriptors; location={}", location);

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            Enumeration<URL> resources = cl.getResources(location);
            if (resources != null && resources.hasMoreElements()) {
                log.debug("Discovered:");
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    log.debug("    {}", url);
                    CommandsXpp3Reader reader = new CommandsXpp3Reader();
                    InputStream input = url.openStream();
                    try {
                        CommandsDescriptor config = reader.read(input);
                        list.add(config);
                    }
                    finally {
                        Closer.close(input);
                    }
                }
            }
        }

        return list;
    }
}