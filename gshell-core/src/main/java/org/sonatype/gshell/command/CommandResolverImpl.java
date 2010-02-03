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

package org.sonatype.gshell.command;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.alias.AliasRegistry;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.cli2.OpaqueArguments;

/**
 * {@link CommandResolver} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
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
                command = command.clone();
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
            return new AliasAction(name, aliasRegistry.getAlias(name));
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

    private static class AliasAction
        extends CommandActionSupport
        implements OpaqueArguments
    {
        private final String target;

        public AliasAction(final String name, final String target) {
            super.setName(name);
            assert target != null;
            this.target = target;
        }

        @Override
        public void setName(final String name) {
            throw new IllegalStateException();
        }

        public Object execute(final CommandContext context) throws Exception {
            assert context != null;

            String alias = target;

            // Need to append any more arguments in the context
            Object[] args = context.getArguments();
            if (args.length > 0) {
                alias = String.format("%s %s", target, Strings.join(args, " "));
            }

            log.debug("Executing alias ({}) -> {}", getName(), alias);

            return context.getShell().execute(alias);
        }
    }
}