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
package org.sonatype.gshell.commands.logging.logger;

import javax.inject.Inject;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.logging.Level;
import org.sonatype.gshell.logging.LoggingSystem;

/**
 * List valid logger levels.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name="logging/logger/levels")
public class LoggerLevelsCommand
    extends CommandActionSupport
{
    private final LoggingSystem logging;

    @Inject
    public LoggerLevelsCommand(final LoggingSystem logging) {
        assert logging != null;
        this.logging = logging;
    }

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        for (Level level : logging.getLevels()) {
            io.println("{}", level);
        }

        return Result.SUCCESS;
    }
}