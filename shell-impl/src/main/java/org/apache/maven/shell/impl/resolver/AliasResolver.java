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

package org.apache.maven.shell.impl.resolver;

import org.apache.maven.shell.Command;
import org.apache.maven.shell.CommandContext;
import org.apache.maven.shell.CommandException;
import org.apache.maven.shell.CommandResolver;
import org.apache.maven.shell.CommandSupport;
import org.apache.maven.shell.registry.AliasRegistry;

/**
 * Provides resolution for aliases.
 *
 * @version $Rev$ $Date$
 */
public class AliasResolver
    implements CommandResolver
{
    // @Requirement
    private AliasRegistry registry;

    public Command resolveCommand(final String name) throws CommandException {
        assert name != null;
        assert registry != null;

        final String alias = registry.getAlias(name);

        return new CommandSupport() {
            public String getName() {
                return name;
            }

            public Object execute(final CommandContext context) throws Exception {
                assert context != null;
                
                log.debug("Executing alias ({}) -> {}", name, alias);

                return context.getShell().execute(alias);
            }
        };
    }
}