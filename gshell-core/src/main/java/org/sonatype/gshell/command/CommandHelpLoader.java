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

package org.sonatype.gshell.command;

import java.io.IOException;

/**
 * Loads command help pages.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.4
 */
public interface CommandHelpLoader
{
    String load(CommandAction command) throws MissingHelpException, IOException;

    class MissingHelpException
        extends Exception
    {
        private Class type;

        public MissingHelpException(final Class type) {
            super("Missing help for: " + type);
            this.type = type;
        }

        public Class getType() {
            return type;
        }
    }
}