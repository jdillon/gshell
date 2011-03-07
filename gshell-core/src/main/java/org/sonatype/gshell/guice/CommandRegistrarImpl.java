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
package org.sonatype.gshell.guice;

import com.google.inject.Key;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.registry.CommandRegistrar;
import org.sonatype.gshell.command.registry.CommandRegistrarSupport;
import org.sonatype.gshell.command.registry.CommandRegistry;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.guice.bean.locators.MutableBeanLocator;
import org.sonatype.inject.BeanEntry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Iterator;

/**
 * Guice {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class CommandRegistrarImpl
    extends CommandRegistrarSupport
{
    private final MutableBeanLocator container;

    private final CommandRegistry registry;

    @Inject
    public CommandRegistrarImpl(final MutableBeanLocator container, final EventManager events, final CommandRegistry registry) {
        super(events);

        assert container != null;
        this.container = container;
        assert registry != null;
        this.registry = registry;
    }

    private Class<?> loadClass(final String className) throws ClassNotFoundException {
        assert className != null;
        return Thread.currentThread().getContextClassLoader().loadClass(className);
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
        Class<?> type = loadClass(className);
        Iterator<BeanEntry<Annotation, ?>> iter = container.locate(Key.get((Class)type)).iterator();
        if (iter.hasNext()) {
            return (CommandAction) iter.next().getValue();
        }
        // This should really never happen
        throw new RuntimeException("Unable to load command action implementation: " + type);
    }
}