/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.command.resolver;

import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.cli2.OpaqueArguments;

/**
 * {@link org.sonatype.gshell.command.CommandAction} to execute an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AliasAction
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