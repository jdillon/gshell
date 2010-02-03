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

package org.sonatype.gshell.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandRegistrar;
import org.sonatype.gshell.command.CommandRegistrarSupport;
import org.sonatype.gshell.command.CommandRegistry;
import org.sonatype.gshell.command.descriptor.CommandSetDescriptor;
import org.sonatype.gshell.command.descriptor.ModuleDescriptor;
import org.sonatype.gshell.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Guice {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class GuiceCommandRegistrar
    extends CommandRegistrarSupport
{
    private final Injector injector;

    private final CommandRegistry registry;

    private final ThreadLocal<Injector> injectorHolder = new ThreadLocal<Injector>() {
        @Override
        protected Injector initialValue() {
            return injector;
        }
    };

    @Inject
    public GuiceCommandRegistrar(final EventManager events, final CommandRegistry registry, final Injector injector) {
        super(events);

        assert registry != null;
        this.registry = registry;
        assert injector != null;
        this.injector = injector;

        log.trace("Base injector: {}", injector);
    }


    @Override
    protected void registerCommandSet(final CommandSetDescriptor config) {
        assert config != null;

        // If we have modules, then configure a new injector to be used for this set
        if (!config.getModules().isEmpty()) {
            log.debug("Building injector for command-set: {}", config);
            injectorHolder.set(buildInjector(config.getModules()));
        }

        try {
            super.registerCommandSet(config);
        }
        finally {
            injectorHolder.set(injector);
        }
    }

    private Class<?> loadClass(final String className) throws ClassNotFoundException {
        assert className != null;
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    @SuppressWarnings({"unchecked"})
    private Injector buildInjector(final List<ModuleDescriptor> config) {
        assert config != null;

        List<Module> modules = new ArrayList<Module>(config.size());
        for (ModuleDescriptor desc : config) {
            String className = desc.getType();
            try {
                Class type = loadClass(className);
                Module module = (Module) injector.getInstance(type);
                log.debug("Loaded module: {}", module);
                modules.add(module);
            }
            catch (Exception e) {
                log.error("Failed to load module: " + className, e);
            }
        }

        Injector child = injector.createChildInjector(modules);
        log.trace("Created child injector: {}", child);

        return child;
    }

    public void registerCommand(final String name, final String className) throws Exception {
        assert name != null;
        assert className != null;

        log.trace("Registering command: {} -> {}", name, className);

        CommandAction command = createAction(className);
        registry.registerCommand(name, command);
    }

    public void registerCommand(final String className) throws Exception {
        assert className != null;

        log.trace("Registering command: {}", className);

        CommandAction command = createAction(className);

        Command meta = command.getClass().getAnnotation(Command.class);
        assert meta != null;
        String name = meta.name();

        registry.registerCommand(name, command);
    }

    @SuppressWarnings({"unchecked"})
    private CommandAction createAction(final String className) throws ClassNotFoundException {
        assert className != null;
        Class type = loadClass(className);
        return (CommandAction) injectorHolder.get().getInstance(type);
    }
}