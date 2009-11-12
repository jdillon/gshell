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

package org.sonatype.gshell.commands;

import com.google.inject.Inject;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.console.completer.AliasNameCompleter;
import org.sonatype.gshell.util.cli.Argument;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.NoSuchAliasException;

/**
 * Undefine an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command
public class UnaliasCommand
    extends CommandActionSupport
{
    private final AliasRegistry aliasRegistry;

    @Argument(index = 0, required = true)
    private String name;

    @Inject
    public UnaliasCommand(final AliasRegistry aliasRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
    }

    @Inject
    public UnaliasCommand installCompleters(final AliasNameCompleter c1) {
        assert c1 != null;
        setCompleters(c1, null);
        return this;
    }

    public Object execute(final CommandContext context) {
        assert context != null;
        IO io = context.getIo();

        log.debug("Un-defining alias: {}", name);

        try {
            aliasRegistry.removeAlias(name);

            return Result.SUCCESS;
        }
        catch (NoSuchAliasException e) {
            io.error(getMessages().format("error.alias-not-defined", name));
            return Result.FAILURE;
        }
    }
}