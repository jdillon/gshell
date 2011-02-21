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
package org.sonatype.gshell.command;

import jline.console.completer.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.util.Strings;
import org.sonatype.gshell.util.cli2.OpaqueArguments;
import org.sonatype.gshell.util.i18n.MessageSource;

/**
 * {@link CommandAction} to execute an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AliasAction
    implements CommandAction, OpaqueArguments
{
    private static final Logger log = LoggerFactory.getLogger(AliasAction.class);

    private final String name;

    private final String target;

    public AliasAction(final String name, final String target) {
        assert name != null;
        this.name = name;
        assert target != null;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
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

        log.debug("Executing alias ({}) -> {}", getName(), alias);

        return context.getShell().execute(alias);
    }

    public MessageSource getMessages() {
        return null;
    }

    public Completer[] getCompleters() {
        return new Completer[0];
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    public CommandAction clone() {
        return this;
    }
}