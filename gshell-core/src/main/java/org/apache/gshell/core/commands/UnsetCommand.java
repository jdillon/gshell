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

package org.apache.gshell.core.commands;

import jline.Completor;
import org.apache.gshell.Variables;
import org.apache.gshell.cli.Argument;
import org.apache.gshell.cli.Option;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.command.CommandActionSupport;
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
 * @since 2.0
 */
@Component(role=UnsetCommand.class)
public class UnsetCommand
    extends CommandActionSupport
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private enum Mode
    {
        VARIABLE,
        PROPERTY
    }

    @Requirement(role=Completor.class, hints={"variable-name"})
    private List<Completor> installCompleters;

    @Option(name="-m", aliases={"--mode"})
    private Mode mode = Mode.VARIABLE;

    @Argument(required=true)
    private List<String> args = null;

    @Override
    public Completor[] getCompleters() {
        if (super.getCompleters() == null) {
            setCompleters(installCompleters);
        }

        return super.getCompleters();
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
