/*
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.logging;

import java.util.Collection;
import java.util.Collections;

/**
 * Provides generic access to the underlying logging system.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class NopLoggingSystem
    implements LoggingSystem
{
    public Level getLevel(String name) {
        return new Level()
        {
            public String getName() {
                return null;
            }
        };
    }

    public Collection<? extends Level> getLevels() {
        return Collections.emptyList();
    }

    public Logger getLogger(String name) {
        return new Logger()
        {
            public String getName() {
                return null;
            }

            public Level getLevel() {
                return null;
            }

            public void setLevel(Level level) {
            }

            public void setLevel(String level) {
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
        return Collections.emptyList();
    }

    public Collection<? extends Component> getComponents() {
        return Collections.emptyList();
    }
}