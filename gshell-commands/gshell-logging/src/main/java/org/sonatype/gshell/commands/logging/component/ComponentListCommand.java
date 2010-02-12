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

package org.sonatype.gshell.commands.logging.component;

import com.google.inject.Inject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.logging.Component;
import org.sonatype.gshell.logging.LoggingSystem;
import org.sonatype.gshell.util.cli2.Option;

/**
 * List components.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="logging/component/list")
public class ComponentListCommand
    extends CommandActionSupport
{
    private final LoggingSystem logging;

    @Option(name="n", longName="name")
    private String nameQuery;

    @Option(name="t", longName="type")
    private String typeQuery;

    @Option(name="v", longName="verbose")
    private boolean verbose;

    @Inject
    public ComponentListCommand(final LoggingSystem logging) {
        assert logging != null;
        this.logging = logging;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        for (Component component : logging.getComponents()) {
            if ((typeQuery == null || component.getType().contains(typeQuery)) &&
                (nameQuery == null || component.getName().contains(nameQuery)))
            {
                io.info("{}", component);
                if (verbose) {
                    io.info("    {}", component.getTarget());
                }
            }
        }

        return Result.SUCCESS;
    }
}