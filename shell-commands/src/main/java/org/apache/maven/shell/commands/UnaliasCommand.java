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

package org.apache.maven.shell.commands;

import org.apache.maven.shell.CommandSupport;
import org.apache.maven.shell.CommandContext;
import org.apache.maven.shell.Command;
import org.apache.maven.shell.io.IO;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.NoSuchAliasException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * The <tt>unalias</tt> command.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Command.class, hint="unalias")
public class UnaliasCommand
    extends CommandSupport
{
    @Requirement
    private AliasRegistry registry;

    public String getName() {
        return "unalias";
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        assert registry != null;
        
        IO io = context.getIo();
        String[] args = context.getArguments();

        if (args.length == 0) {
            io.error("Command requires one or more arguments");
        }
        else {
            for (String arg : args) {
                try {
                    registry.removeAlias(arg);
                }
                catch (NoSuchAliasException e) {
                    io.error("{}", e);
                }
            }
        }

        return Result.SUCCESS;
    }
}