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

package org.sonatype.gshell.commands.logging.logger;

import javax.inject.Inject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.logging.Logger;
import org.sonatype.gshell.logging.LoggingSystem;
import org.sonatype.gshell.util.cli2.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List loggers.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="logging/logger/list")
public class LoggerListCommand
    extends CommandActionSupport
{
    private final LoggingSystem logging;

    @Option(name="n", longName="name")
    private String nameQuery;


    @Option(name="l", longName="level")
    private String levelQuery;

    @Option(name="a", longName="all")
    private boolean all;

    @Inject
    public LoggerListCommand(final LoggingSystem logging) {
        assert logging != null;
        this.logging = logging;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        List<String> names = new ArrayList<String>();
        names.addAll(logging.getLoggerNames());
        Collections.sort(names);
        
        for (String name : names) {
            if (nameQuery == null || name.contains(nameQuery)) {
                Logger logger = logging.getLogger(name);
                if (all || logger.getLevel() != null &&
                    (levelQuery == null || logger.getLevel().toString().contains(levelQuery.toUpperCase())))
                {
                    io.println("{}: {}", logger.getName(), logger.getLevel());
                }
            }
        }

        return Result.SUCCESS;
    }
}