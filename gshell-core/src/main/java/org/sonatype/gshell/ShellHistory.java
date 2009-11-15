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

package org.sonatype.gshell;

import jline.console.history.FileHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of {@link History} for <a href="http://jline.sf.net">JLine</a>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellHistory
    extends FileHistory
    implements History, VariableNames
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ShellHistory(final File file) throws IOException {
        super(file);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                try {
                    flush();
                }
                catch (IOException e) {
                    log.error("Failed to flush history", e);
                }
            }
        });
    }
}