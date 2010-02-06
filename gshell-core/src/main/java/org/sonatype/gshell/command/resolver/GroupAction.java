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

import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.OpaqueArguments;

import static org.sonatype.gshell.vars.VariableNames.SHELL_GROUP;

/**
 * {@link org.sonatype.gshell.command.CommandAction} to switch groups.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class GroupAction
    extends CommandActionSupport
    implements OpaqueArguments
{
    // Maybe make --help run '/help <name>' ?
    
    public GroupAction(final String name) {
        super.setName(name);
    }

    @Override
    public void setName(final String name) {
        throw new IllegalStateException();
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        // TODO: Consider trying to use the Node so it can cache?
        log.debug("Changing group to: {}", getName());
        context.getVariables().set(SHELL_GROUP, getName());

        return Result.SUCCESS;
    }
}