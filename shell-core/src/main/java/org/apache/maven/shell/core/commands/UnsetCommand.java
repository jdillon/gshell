/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.core.commands;

import jline.Completor;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Unset a variable or property.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
@Component(role=Command.class, hint="unset")
public class UnsetCommand
    extends CommandSupport
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private enum Mode
    {
        VARIABLE,
        PROPERTY
    }

    @Requirement(role=Completor.class, hints={"variable-name"})
    private List<Completor> completers;

    @Option(name="-m", aliases={"--mode"})
    private Mode mode = Mode.VARIABLE;

    @Argument(required=true)
    private List<String> args = null;

    @Override
    public Completor[] getCompleters() {
        assert completers != null;

        return new Completor[] {
            new AggregateCompleter(completers),
            null
        };
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        Variables variables = context.getVariables();

        for (String arg : args) {
            String namevalue = String.valueOf(arg);

            switch (mode) {
                case PROPERTY:
                    unsetProperty(namevalue);
                    break;

                case VARIABLE:
                    unsetVariable(variables, namevalue);
                    break;
            }
        }

        return Result.SUCCESS;
    }

    private void unsetProperty(final String name) {
        log.info("Unsetting system property: {}", name);

        System.getProperties().remove(name);
    }

    private void unsetVariable(final Variables vars, final String name) {
        log.info("Unsetting variable: {}", name);

        vars.unset(name);
    }
}
