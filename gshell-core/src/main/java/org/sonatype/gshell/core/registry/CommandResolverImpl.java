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

package org.sonatype.gshell.core.registry;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.cli.OpaqueArguments;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.CommandException;
import org.sonatype.gshell.core.command.CommandActionSupport;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.registry.CommandResolver;
import org.sonatype.gshell.util.Strings;

/**
 * The default {@link org.sonatype.gshell.registry.CommandResolver} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class CommandResolverImpl
    implements CommandResolver
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AliasRegistry aliasRegistry;

    private final CommandRegistry commandRegistry;

    @Inject
    public CommandResolverImpl(final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    public CommandAction resolveCommand(final String name) throws CommandException {
        assert name != null;

        log.trace("Resolving command: {}", name);

        CommandAction command = resolveAlias(name);
        if (command == null) {
            command = resolveRegistered(name);
            if (command != null) {
                // Copy the prototype to use
                command = command.copy();
            }
        }

        if (command == null) {
            throw new CommandException("Unable to resolve command: " + name);
        }

        log.trace("Resolved command: {}", command);

        return command;
    }

    private CommandAction resolveAlias(final String name) throws CommandException {
        assert name != null;
        assert aliasRegistry != null;

        if (aliasRegistry.containsAlias(name)) {
            return new Alias(name, aliasRegistry.getAlias(name));
        }

        return null;
    }

    private CommandAction resolveRegistered(final String name) throws CommandException {
        assert name != null;
        assert commandRegistry != null;

        if (commandRegistry.containsCommand(name)) {
            return commandRegistry.getCommand(name);
        }

        return null;
    }

    //
    // Alias
    //

    private static class Alias
        extends CommandActionSupport
        implements OpaqueArguments
    {
        private final String name;

        private final String target;

        public Alias(final String name, final String target) {
            assert name != null;
            assert target != null;

            this.name = name;
            this.target = target;
        }

        @Override
        public String getName() {
            return name;
        }

        public Object execute(final CommandContext context) throws Exception {
            assert context != null;

            String alias = target;

            // Need to append any more arguments in the context
            Object[] args = context.getArguments();
            if (args.length > 0) {
                alias = String.format("%s %s", target, Strings.join(args, " "));
            }

            log.debug("Executing alias ({}) -> {}", name, alias);

            return context.getShell().execute(alias);
        }
    }
}