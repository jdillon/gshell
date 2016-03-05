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
package org.sonatype.gshell.help;

import java.io.IOException;

/**
 * Loads help page contents.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public interface HelpContentLoader
{
    String load(String name, ClassLoader loader) throws MissingContentException, IOException;

    class MissingContentException
        extends Exception
    {
        private final String name;

        public MissingContentException(final String name) {
            super("Missing content for: " + name);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}