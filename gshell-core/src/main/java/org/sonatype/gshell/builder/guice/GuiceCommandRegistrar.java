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

package org.sonatype.gshell.builder.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.registry.CommandRegistrarSupport;
import org.sonatype.gshell.registry.CommandRegistry;

/**
 * Guice {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class GuiceCommandRegistrar
    extends CommandRegistrarSupport
{
    private final Injector injector;

    private final CommandRegistry registry;

    @Inject
    public GuiceCommandRegistrar(final CommandRegistry registry, final Injector injector) {
        assert registry != null;
        this.registry = registry;
        assert injector != null;
        this.injector = injector;
    }

    public void registerCommand(final String name, final String classname) throws Exception {
        assert name != null;
        assert classname != null;

        log.trace("Registering command: {} -> {}", name, classname);

        Class<CommandAction> type = (Class<CommandAction>) Thread.currentThread().getContextClassLoader().loadClass(classname);
        CommandAction command = injector.getInstance(type);
        registry.registerCommand(name, command);
    }

    public void registerCommand(final String classname) throws Exception {
        assert classname != null;

        log.trace("Registering command: {}", classname);

        Class<CommandAction> type = (Class<CommandAction>) Thread.currentThread().getContextClassLoader().loadClass(classname);
        CommandAction command = injector.getInstance(type);

        Command meta = type.getAnnotation(Command.class);
        assert meta != null;
        String name = meta.name();

        registry.registerCommand(name, command);
    }
}