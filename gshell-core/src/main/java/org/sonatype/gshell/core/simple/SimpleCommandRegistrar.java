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

package org.sonatype.gshell.core.simple;

import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.registry.CommandRegistrarSupport;
import org.sonatype.gshell.registry.CommandRegistry;

/**
 * Simple {@link org.sonatype.gshell.registry.CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class SimpleCommandRegistrar
    extends CommandRegistrarSupport
{
    private final CommandRegistry commandRegistry;

    public SimpleCommandRegistrar(final CommandRegistry commandRegistry) {
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void registerCommands() throws Exception {
        // Nothing
    }

    public void registerCommand(final String name, final String className) throws Exception {
        assert name != null;
        assert className != null;

        Class type = Thread.currentThread().getContextClassLoader().loadClass(className);
        CommandAction command = (CommandAction) type.newInstance();

        commandRegistry.registerCommand(name, command);
    }
}