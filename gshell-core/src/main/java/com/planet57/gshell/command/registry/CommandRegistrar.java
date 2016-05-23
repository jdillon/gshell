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
package com.planet57.gshell.command.registry;

import java.util.List;

import com.planet57.gshell.command.descriptor.CommandSetDescriptor;

/**
 * Registers commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public interface CommandRegistrar
{
    /**
     * @since 2.1
     */
    String DEFAULT_DESCRIPTOR_LOCATION = "META-INF/com.planet57.gshell/commands.xml";

    void registerCommands() throws Exception;

    void registerCommand(String name, String type) throws Exception;

    void registerCommand(String type) throws Exception;

    /**
     * @since 2.2
     */
    String[] getDescriptorSearchPath();

    /**
     * @since 2.2
     */
    void setDescriptorSearchPath(String... path);

    /**
     * @since 2.1
     */
    List<CommandSetDescriptor> getDescriptors();
}