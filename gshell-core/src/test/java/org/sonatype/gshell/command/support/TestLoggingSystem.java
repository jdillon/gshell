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

package org.sonatype.gshell.command.support;

import org.sonatype.gshell.logging.Component;
import org.sonatype.gshell.logging.Level;
import org.sonatype.gshell.logging.Logger;
import org.sonatype.gshell.logging.LoggingSystem;

import java.util.Collection;
import java.util.Collections;

/**
 * Test {@link LoggingSystem}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestLoggingSystem
    implements LoggingSystem
{
    public Level getLevel(final String name) {
        return new Level()
        {
            public String getName() {
                return name;
            }
        };
    }

    public Collection<? extends Level> getLevels() {
        return Collections.emptySet();
    }

    public Logger getLogger(final String name) {
        return new Logger()
        {
            public String getName() {
                return name;
            }

            public Level getLevel() {
                return null;
            }

            public void setLevel(Level level) {
                // ignore
            }

            public void setLevel(String level) {
                // ignore
            }

            public Logger parent() {
                return null;
            }

            public boolean isRoot() {
                return false;
            }
        };
    }

    public Collection<String> getLoggerNames() {
        return Collections.emptySet();
    }

    public Collection<? extends Component> getComponents() {
        return Collections.emptySet();
    }
}