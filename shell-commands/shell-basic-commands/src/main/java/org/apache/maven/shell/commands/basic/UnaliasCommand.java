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

package org.apache.maven.shell.commands.basic;

import jline.Completor;
import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.NoSuchAliasException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.List;

/**
 * Undefine an alias.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Command.class, hint="unalias")
public class UnaliasCommand
    extends CommandSupport
{
    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement(role=Completor.class, hints={"variable-name"})
    private List<Completor> completers;

    @Argument(index=0, required=true)
    private String name;

    public UnaliasCommand() {}

    public UnaliasCommand(final AliasRegistry aliasRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
    }

    @Override
    public Completor[] getCompleters() {
        assert completers != null;

        return new Completor[] {
            new AggregateCompleter(completers),
            null
        };
    }
    
    public Object execute(final CommandContext context) {
        assert context != null;
        IO io = context.getIo();

        log.debug("Undefining alias: {}", name);

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