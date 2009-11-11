/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.commands.shell;

import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.util.cli.Argument;

// FIXME: This belongs in gshell-shell

/**
 * Sleep for a period.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command
public class SleepCommand
    extends CommandActionSupport
{
    @Argument(required=true)
    private long time;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;

        if (log.isDebugEnabled()) {
            log.debug("Sleeping for {} on thread: {}", time, Thread.currentThread());
        }
        else {
            log.info("Sleeping for {}", time);
        }

        try {
            Thread.sleep(time);
        }
        catch (InterruptedException ignore) {
            log.debug("Sleep was interrupted... :-(");
            return Result.FAILURE;
        }

        log.info("Awake now");

        return Result.SUCCESS;
    }
}
