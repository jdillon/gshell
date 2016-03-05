/*
 * Copyright (c) 2009-present the original author or authors.
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
package org.sonatype.gshell.commands.standard;

import javax.inject.Inject;
import javax.inject.Named;
import jline.console.completer.Completer;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;
import org.sonatype.gshell.variables.Variables;

import java.util.List;

/**
 * Unset a variable or property.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="unset")
public class UnsetCommand
    extends CommandActionSupport
{
    @Option(name = "m", longName = "mode")
    private SetCommand.Mode mode = SetCommand.Mode.VARIABLE;

    @Argument(required = true)
    private List<String> args;

    @Inject
    public UnsetCommand installCompleters(@Named("variable-name") final Completer c1) {
        assert c1 != null;
        setCompleters(c1, null);
        return this;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        Variables variables = context.getVariables();

        for (String arg : args) {
            String name = String.valueOf(arg);

            switch (mode) {
                case PROPERTY:
                    unsetProperty(name);
                    break;

                case VARIABLE:
                    unsetVariable(variables, name);
                    break;
            }
        }

        return Result.SUCCESS;
    }

    private void unsetProperty(final String name) {
        log.info("Un-setting system property: {}", name);

        System.getProperties().remove(name);
    }

    private void unsetVariable(final Variables vars, final String name) {
        log.info("Un-setting variable: {}", name);

        vars.unset(name);
    }
}
