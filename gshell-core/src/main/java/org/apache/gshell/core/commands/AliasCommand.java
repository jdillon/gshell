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

import org.apache.gshell.cli.Argument;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.io.IO;
import org.apache.gshell.registry.AliasRegistry;
import org.apache.gshell.util.Strings;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.Collection;
import java.util.List;

/**
 * Define an alias or list defined aliases.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Component(role=AliasCommand.class)
public class AliasCommand
    extends CommandActionSupport
{
    @Requirement
    private AliasRegistry aliasRegistry;
    
    @Argument(index=0)
    private String name;

    @Argument(index=1, multiValued=true)
    private List<String> target = null;

    public AliasCommand() {}

    public AliasCommand(final AliasRegistry aliasRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        if (name == null) {
            return listAliases(context);
        }
        else {
            return defineAlias(context);
        }
    }

    private Object listAliases(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        log.debug("Listing defined aliases");

        Collection<String> names = aliasRegistry.getAliasNames();

        if (names.isEmpty()) {
            io.info(getMessages().format("info.no-aliases"));
        }
        else {
            // Determine the maximum name length
            int maxNameLen = 0;
            for (String name : names) {
                if (name.length() > maxNameLen) {
                    maxNameLen = name.length();
                }
            }

            io.out.println(getMessages().format("info.defined-aliases"));
            String nameFormat = "%-" + maxNameLen + 's';

            for (String name : names) {
                String alias = aliasRegistry.getAlias(name);
                String formattedName = String.format(nameFormat, name);

                io.out.format("  @|bold %s|  ", formattedName);
                io.out.println(getMessages().format("info.alias-to", alias));
            }
        }

        return Result.SUCCESS;
    }

    private Object defineAlias(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (target == null) {
            io.error(getMessages().format("error.missing-arg"), getMessages().getMessage("command.argument.target.token"));
            return Result.FAILURE;
        }

        String alias = Strings.join(target.toArray(), " ");
        
        log.debug("Defining alias: {} -> {}", name, alias);

        aliasRegistry.registerAlias(name, alias);

        return Result.SUCCESS;
    }
}